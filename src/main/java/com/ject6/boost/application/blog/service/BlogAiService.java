package com.ject6.boost.application.blog.service;

import com.ject6.boost.application.blog.exception.BlogErrorCode;
import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.infrastructure.common.queue.AnalysisQueuePublisher;
import com.ject6.boost.domain.blog.repository.BlogRecommendationRepository;
import com.ject6.boost.domain.campaign.entity.Campaign;
import com.ject6.boost.domain.campaign.repository.CampaignRepository;
import com.ject6.boost.infrastructure.blog.client.CrawlerClient;
import com.ject6.boost.infrastructure.blog.client.PythonAiClient;
import com.ject6.boost.infrastructure.blog.client.dto.ConversationRequest;
import com.ject6.boost.infrastructure.blog.client.dto.ConversationResponse;
import com.ject6.boost.presentation.blog.dto.*;
import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.entity.UserBlog;
import com.ject6.boost.domain.user.repository.BlogAnalysisResultRepository;
import com.ject6.boost.domain.user.repository.UserBlogRepository;
import com.ject6.boost.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogAiService {

    private static final int FREE_PLAN_VISIBLE_COUNT = 3;
    private static final int RECOMMENDATION_LIMIT = 8;
    private static final Set<String> VALID_MODES = Set.of("FULL_BLOG", "POST");

    private final AnalysisQueuePublisher queuePublisher;
    private final PythonAiClient pythonAiClient;
    private final CrawlerClient crawlerClient;
    private final BlogCrawlerAsyncTrigger crawlerAsyncTrigger;
    private final BlogAnalysisResultRepository blogAnalysisResultRepository;
    private final BlogRecommendationRepository blogRecommendationRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final UserBlogRepository userBlogRepository;

    /**
     * POST /blog/analyze
     * 연결된 블로그를 검증하고, 분석 모드에 따라 크롤러를 트리거하거나 분석 큐에 발행합니다.
     *
     * FULL_BLOG: 전체 포스트를 크롤링·집계 후 BLOG_SNAPSHOT 1건 분석 (기본값)
     * POST:      개별 포스트 분석 — documentId 있으면 즉시 큐 발행, 없으면 크롤링 후 포스트별 분석
     */
    @Transactional
    public AnalyzeResponse requestAnalysis(Long userId, AnalyzeRequest request) {
        UserBlog blog = userRepository.findActiveById(userId)
                .flatMap(user -> userBlogRepository.findActiveByUser(user).stream().findFirst())
                .orElseThrow(() -> new BusinessException(BlogErrorCode.BLOG_NOT_CONNECTED));

        String correlationId = UUID.randomUUID().toString();
        String rawMode = request.analysisMode();
        String mode = (rawMode != null) ? rawMode.trim().toUpperCase() : "FULL_BLOG";
        if (!VALID_MODES.contains(mode)) {
            throw new BusinessException(BlogErrorCode.INVALID_ANALYSIS_MODE);
        }

        if ("FULL_BLOG".equals(mode)) {
            // FULL_BLOG는 여러 포스트를 먼저 모두 ingest한 뒤 블로그 전체 스냅샷 1건만 분석한다.
            // batchId는 이 요청에서 크롤링된 N개 포스트를 하나의 묶음으로 추적하기 위한 ID다.
            // 여기서 batch는 스케줄러/야간 배치가 아니라 "FULL_BLOG 요청 1건의 포스트 묶음"을 뜻한다.
            String batchId = UUID.randomUUID().toString();
            crawlerAsyncTrigger.triggerAsync(blog.getBlogUrl(), userId, blog.getId(), correlationId, "FULL_BLOG", batchId);
            log.info("FULL_BLOG 분석 요청 userId={} correlationId={} batchId={}", userId, correlationId, batchId);
            return new AnalyzeResponse(null, "crawling",
                    "블로그 전체 분석을 준비 중입니다. 크롤링 및 집계 완료 후 분석이 시작됩니다.",
                    null, correlationId, batchId);
        }

        // POST mode
        Long docId = request.documentId();
        if (docId != null) {
            queuePublisher.publishWithMode(userId, docId, correlationId, "POST");
            try {
                blogAnalysisResultRepository.save(BlogAnalysisResult.create(
                        userRepository.findActiveById(userId).orElseThrow(), blog, docId));
            } catch (Exception e) {
                log.warn("blog_analysis_results 저장 실패 (non-critical) userId={}: {}", userId, e.getMessage());
            }
            return new AnalyzeResponse(docId, "pending", "분석이 요청되었습니다.", null, correlationId, null);
        }

        // POST + documentId 없음 → 크롤링 후 IngestWorker가 포스트별 분석 큐 발행
        crawlerAsyncTrigger.triggerAsync(blog.getBlogUrl(), userId, blog.getId(), correlationId, "POST", null);
        log.info("POST 분석 요청 (documentId 없음) userId={} correlationId={}", userId, correlationId);
        return new AnalyzeResponse(null, "crawling", "블로그 크롤링 중입니다. 잠시 후 분석이 시작됩니다.",
                null, correlationId, null);
    }

    /**
     * GET /blog/analysis/{documentId}
     * HTTP — Python analysis_jobs 테이블에서 조회
     */
    public BlogAnalysisDetailResponse getAnalysis(Long documentId) {
        return BlogAnalysisDetailResponse.from(pythonAiClient.getAnalysis(documentId));
    }

    /**
     * GET /blog/analysis/{id}/recommendations
     * pgvector 유사도 검색 → 결과 없으면 active 캠페인 fallback
     */
    public RecommendedCampaignResponse getRecommendations(Long userId, Long analysisId) {
        List<RecommendedCampaignResponse.CampaignItem> campaigns;
        try {
            campaigns = blogRecommendationRepository.findRecommendedCampaigns(userId, analysisId, RECOMMENDATION_LIMIT);
        } catch (Exception e) {
            log.warn("pgvector 추천 쿼리 실패, fallback 사용 userId={} analysisId={}: {}", userId, analysisId, e.getMessage());
            campaigns = List.of();
        }

        if (campaigns.isEmpty()) {
            log.info("추천 결과 없음 — fallback 사용 userId={} analysisId={}", userId, analysisId);
            List<Campaign> fallback = campaignRepository.findActiveFallback(RECOMMENDATION_LIMIT);
            campaigns = fallback.stream()
                    .map(c -> new RecommendedCampaignResponse.CampaignItem(
                            c.getId(), c.getTitle(), 70, 70,
                            "FALLBACK", "AI 분석 기반 추천 공고입니다."
                    ))
                    .toList();
        }
        return new RecommendedCampaignResponse(analysisId, campaigns);
    }

    /**
     * GET /blog/analysis/{id}/bloggers
     * 카테고리별 인기 블로거 Top3 (추후 구현)
     */
    public BloggerResponse getBloggers(Long userId, Long analysisId) {
        BlogRecommendationRepository.BloggerCandidates candidates =
                blogRecommendationRepository.findBloggerCandidates(userId, analysisId, 3);
        return new BloggerResponse(candidates.category(), candidates.bloggers());
    }

    /**
     * GET /blog/analysis/history
     * Spring 자체 blog_analysis_results 테이블 조회
     */
    @Transactional(readOnly = true)
    public BlogAnalysisHistoryResponse getHistory(Long userId, boolean isPremium) {
        List<BlogAnalysisResult> results = blogAnalysisResultRepository.findByUserIdAndDeletedAtIsNull(userId);
        int visibleCount = isPremium ? results.size() : Math.min(results.size(), FREE_PLAN_VISIBLE_COUNT);

        List<BlogAnalysisHistoryResponse.HistoryItem> items = IntStream.range(0, results.size())
                .mapToObj(i -> {
                    BlogAnalysisResult r = results.get(i);
                    boolean locked = !isPremium && i >= FREE_PLAN_VISIBLE_COUNT;
                    String channelUrl = r.getBlog() != null ? r.getBlog().getBlogUrl() : null;
                    return new BlogAnalysisHistoryResponse.HistoryItem(r.getId(), channelUrl, r.getCreatedAt(), locked);
                })
                .toList();

        return new BlogAnalysisHistoryResponse(items, results.size(), visibleCount);
    }

    /**
     * POST /blog/chat
     * HTTP 동기 프록시 → Python POST /v1/conversations/messages
     */
    public ChatResponse chat(Long userId, ChatRequest request) {
        ConversationResponse resp = pythonAiClient.sendChat(
                new ConversationRequest(userId, request.sessionId(), request.documentId(), request.message())
        );
        return new ChatResponse(resp.sessionId(), resp.reply(), resp.tokensUsed(), resp.tokensRemaining());
    }

    /**
     * DELETE /blog/chat/{sessionId}
     * HTTP 동기 프록시 → Python DELETE /v1/conversations/{sessionId}
     */
    public void resetSession(String sessionId) {
        pythonAiClient.resetSession(sessionId);
    }
}
