package com.ject6.boost.application.blog.service;

import com.ject6.boost.infrastructure.common.queue.AnalysisQueuePublisher;
import com.ject6.boost.domain.blog.repository.BlogRecommendationRepository;
import com.ject6.boost.domain.campaign.entity.Campaign;
import com.ject6.boost.domain.campaign.repository.CampaignRepository;
import com.ject6.boost.infrastructure.blog.client.PythonAiClient;
import com.ject6.boost.infrastructure.blog.client.dto.ConversationRequest;
import com.ject6.boost.infrastructure.blog.client.dto.ConversationResponse;
import com.ject6.boost.presentation.blog.dto.*;
import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.repository.BlogAnalysisResultRepository;
import com.ject6.boost.domain.user.repository.UserBlogRepository;
import com.ject6.boost.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogAiService {

    private static final int FREE_PLAN_VISIBLE_COUNT = 3;
    private static final int RECOMMENDATION_LIMIT = 8;

    private final AnalysisQueuePublisher queuePublisher;
    private final PythonAiClient pythonAiClient;
    private final BlogAnalysisResultRepository blogAnalysisResultRepository;
    private final BlogRecommendationRepository blogRecommendationRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final UserBlogRepository userBlogRepository;

    private static final long DEMO_DOCUMENT_ID = 1001L;

    /**
     * POST /blog/analyze
     * documentId 미전달 시 해당 유저 최초 문서 ID를 사용 (데모 fallback).
     */
    @Transactional
    public AnalyzeResponse requestAnalysis(Long userId, AnalyzeRequest request) {
        Long docId = request.documentId();
        if (docId == null) {
            docId = DEMO_DOCUMENT_ID;
            log.info("documentId 미전달 — demo fallback 사용 userId={}", userId);
        }
        queuePublisher.publish(userId, docId);

        final Long finalDocId = docId;
        userRepository.findActiveById(userId).ifPresent(user ->
            userBlogRepository.findActiveByUser(user).stream().findFirst().ifPresent(blog -> {
                try {
                    blogAnalysisResultRepository.save(BlogAnalysisResult.create(user, blog, finalDocId));
                } catch (Exception e) {
                    log.warn("blog_analysis_results 저장 실패 (non-critical) userId={}: {}", userId, e.getMessage());
                }
            })
        );

        return new AnalyzeResponse(docId, "pending", "분석이 요청되었습니다.", null);
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
