package com.ject6.boost.infrastructure.blog.client;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class CrawlerClient {

    private final RestClient restClient;

    public CrawlerClient(@Value("${crawler.url:http://localhost:8001}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void triggerBlogPostCrawl(String blogUrl) {
        try {
            restClient.post()
                    .uri("/crawl/blog-posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("blog_url", blogUrl))
                    .retrieve()
                    .toBodilessEntity();
            log.info("crawler: blog post crawl triggered blogUrl={}", blogUrl);
        } catch (Exception e) {
            log.warn("crawler: blog post crawl trigger 실패 (best-effort) blogUrl={}: {}", blogUrl, e.getMessage());
        }
    }
}
