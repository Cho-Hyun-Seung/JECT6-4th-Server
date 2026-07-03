package com.ject6.boost.infrastructure.blog.listener;

import com.ject6.boost.application.blog.service.DiagnosisQuotaService;
import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.entity.UserBlog;
import com.ject6.boost.domain.user.repository.BlogAnalysisResultRepository;
import com.ject6.boost.domain.user.repository.UserBlogRepository;
import com.ject6.boost.domain.user.repository.UserRepository;
import com.ject6.boost.infrastructure.common.config.RabbitMQConfig;
import com.ject6.boost.infrastructure.common.queue.AnalysisCompletedMessage;
import com.ject6.boost.infrastructure.common.redis.AnalysisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Analyzer가 발행한 blog.analysis.completed 이벤트를 수신.
 *   1. 멱등성 검사 — correlationId 기반 중복 수신 방어 (B-1)
 *   2. SUCCESS일 때만 Redis 분석 캐시 포인터 저장 (A-2)
 *   3. 의미 기반 lock 해제 — correlationId → {userId, blogId} 컨텍스트로 역산 (A-1, A-3)
 *   4. R3 진단 쿼터 확정 차감 (correlationId 멱등 보장, B-2)
 *   5. FULL_BLOG 성공 시 blog_analysis_results에 히스토리 항목 저장 (POST 모드는 요청 시점에 이미 저장됨)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisCompletedListener {

    private final AnalysisCacheService cacheService;
    private final DiagnosisQuotaService quotaService;
    private final BlogAnalysisResultRepository blogAnalysisResultRepository;
    private final UserRepository userRepository;
    private final UserBlogRepository userBlogRepository;

    @RabbitListener(queues = RabbitMQConfig.COMPLETED_QUEUE)
    public void handle(AnalysisCompletedMessage message) {
        log.info("analysis.completed received userId={} documentId={} mode={} status={}",
                message.userId(), message.documentId(), message.analysisMode(), message.status());

        // B-1: 멱등성 검사 — 동일 correlationId 이벤트는 단 한 번만 처리
        String correlationId = message.correlationId();
        if (correlationId != null) {
            if (!cacheService.markEventProcessed(correlationId)) {
                log.warn("duplicate completed event skipped correlationId={}", correlationId);
                return;
            }
        }

        boolean isSuccess = "SUCCESS".equalsIgnoreCase(message.status());
        boolean isFullBlog = "FULL_BLOG".equalsIgnoreCase(message.analysisMode());
        boolean isDiagnosisMode = isFullBlog || "DIAGNOSIS".equalsIgnoreCase(message.analysisMode());

        // A-2: SUCCESS일 때만 캐시 저장
        if (isSuccess) {
            try {
                if (isFullBlog) {
                    Long blogId = resolveBlogId(correlationId, message.blogId());
                    if (blogId != null && message.userId() != null && message.analysisJobId() != null) {
                        cacheService.putFullBlogCache(message.userId(), blogId, message.analysisJobId());
                        saveHistoryEntry(message.userId(), blogId, message.documentId());
                    } else {
                        log.warn("FULL_BLOG cache skipped: missing blogId or jobId correlationId={}", correlationId);
                    }
                } else {
                    if (message.userId() != null && message.documentId() != null && message.analysisJobId() != null) {
                        cacheService.putPostCache(message.userId(), message.documentId(), message.analysisJobId());
                    }
                }
            } catch (Exception e) {
                log.warn("cache store failed userId={} err={}", message.userId(), e.getMessage());
            }
        }

        // A-1 + A-3: 의미 기반 lock 해제 (FULL_BLOG만 lock을 잡음)
        if (isFullBlog && correlationId != null) {
            long[] ctx = cacheService.getCorrelationContext(correlationId);
            if (ctx != null) {
                cacheService.releaseFullBlogLock(ctx[0], ctx[1]);
                cacheService.deleteCorrelationContext(correlationId);
            } else if (message.userId() != null && message.blogId() != null) {
                cacheService.releaseFullBlogLock(message.userId(), message.blogId());
            }
        }

        // B-2: 쿼터 차감 (correlationId 전달로 멱등성 보장)
        if (isDiagnosisMode && message.userId() != null) {
            if (isSuccess) {
                quotaService.confirmUsed(message.userId(), correlationId);
            } else {
                quotaService.releaseReservation(message.userId());
            }
        }
    }

    /**
     * FULL_BLOG 분석 완료 시 히스토리 목록(blog_analysis_results)에 항목을 남긴다.
     * POST 모드는 요청 시점에 BlogAiService에서 이미 저장하지만, FULL_BLOG는 완료 시점에야
     * documentId가 확정되므로 여기서 저장해야 한다 (이전에는 이 저장 자체가 누락되어 있었음).
     */
    private void saveHistoryEntry(Long userId, Long blogId, Long documentId) {
        if (documentId == null) {
            log.warn("FULL_BLOG history skipped: documentId 없음 userId={} blogId={}", userId, blogId);
            return;
        }
        try {
            User user = userRepository.findActiveById(userId).orElseThrow();
            UserBlog blog = userBlogRepository.findActiveByUser(user).stream()
                    .filter(b -> b.getId().equals(blogId))
                    .findFirst()
                    .orElseThrow();
            blogAnalysisResultRepository.save(BlogAnalysisResult.create(user, blog, documentId));
        } catch (Exception e) {
            log.warn("blog_analysis_results 저장 실패 (FULL_BLOG) userId={} blogId={}: {}", userId, blogId, e.getMessage());
        }
    }

    /** FULL_BLOG 캐시 키에 필요한 blogId를 확정한다. Redis 컨텍스트 우선, 없으면 payload 값 사용. */
    private Long resolveBlogId(String correlationId, Long payloadBlogId) {
        if (correlationId != null) {
            long[] ctx = cacheService.getCorrelationContext(correlationId);
            if (ctx != null) return ctx[1];
        }
        return payloadBlogId;
    }
}
