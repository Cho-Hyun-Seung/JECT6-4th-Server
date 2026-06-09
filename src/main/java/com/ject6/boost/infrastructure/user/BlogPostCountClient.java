package com.ject6.boost.infrastructure.user;

import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.application.user.exception.UserErrorCode;
import com.ject6.boost.domain.user.constant.BlogPlatform;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.w3c.dom.Document;

@Component
@RequiredArgsConstructor
public class BlogPostCountClient {

    private final RestClient restClient = RestClient.create();

    public int countPosts(String blogUrl, BlogPlatform platform) {
        if (platform != BlogPlatform.NAVER) {
            throw new BusinessException(UserErrorCode.INVALID_BLOG_PLATFORM);
        }

        String blogId = extractNaverBlogId(blogUrl);
        String rss = requestNaverRss(blogId);
        return countRssItems(rss);
    }

    private String extractNaverBlogId(String blogUrl) {
        try {
            URI uri = URI.create(blogUrl.trim());
            if (!"https".equalsIgnoreCase(uri.getScheme()) || !"blog.naver.com".equalsIgnoreCase(uri.getHost())) {
                throw new BusinessException(UserErrorCode.INVALID_BLOG_URL);
            }

            String path = uri.getPath();
            if (!StringUtils.hasText(path)) {
                throw new BusinessException(UserErrorCode.INVALID_BLOG_URL);
            }

            String[] segments = path.split("/");
            for (String segment : segments) {
                if (StringUtils.hasText(segment)) {
                    return segment;
                }
            }
            throw new BusinessException(UserErrorCode.INVALID_BLOG_URL);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(UserErrorCode.INVALID_BLOG_URL);
        }
    }

    private String requestNaverRss(String blogId) {
        try {
            return restClient.get()
                    .uri("https://rss.blog.naver.com/{blogId}.xml", blogId)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException exception) {
            throw new BusinessException(UserErrorCode.BLOG_POST_COUNT_INSUFFICIENT);
        }
    }

    private int countRssItems(String rss) {
        if (!StringUtils.hasText(rss)) {
            throw new BusinessException(UserErrorCode.BLOG_POST_COUNT_INSUFFICIENT);
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(rss.getBytes(StandardCharsets.UTF_8)));
            return document.getElementsByTagName("item").getLength();
        } catch (Exception exception) {
            throw new BusinessException(UserErrorCode.BLOG_POST_COUNT_INSUFFICIENT);
        }
    }
}
