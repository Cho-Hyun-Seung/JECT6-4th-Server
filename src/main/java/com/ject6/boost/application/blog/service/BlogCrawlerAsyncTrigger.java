package com.ject6.boost.application.blog.service;

import com.ject6.boost.infrastructure.blog.client.CrawlerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlogCrawlerAsyncTrigger {

    private final CrawlerClient crawlerClient;

    @Async("blogCrawlerExecutor")
    public void triggerAsync(String blogUrl, Long userId, Long blogId,
                             String correlationId, String analysisMode, String batchId) {
        log.debug("async crawl trigger mode={} userId={} batchId={}", analysisMode, userId, batchId);
        crawlerClient.triggerBlogPostCrawl(blogUrl, userId, blogId, correlationId, analysisMode, batchId);
    }
}
