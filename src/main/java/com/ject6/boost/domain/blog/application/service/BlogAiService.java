package com.ject6.boost.domain.blog.application.service;

import com.ject6.boost.common.queue.AnalysisQueuePublisher;
import com.ject6.boost.domain.blog.domain.repository.BlogRecommendationRepository;
import com.ject6.boost.domain.blog.infrastructure.client.PythonAiClient;
import com.ject6.boost.domain.blog.infrastructure.client.dto.ConversationRequest;
import com.ject6.boost.domain.blog.infrastructure.client.dto.ConversationResponse;
import com.ject6.boost.domain.blog.presentation.dto.*;
import com.ject6.boost.domain.user.domain.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.domain.repository.BlogAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BlogAiService {

    private static final int FREE_PLAN_VISIBLE_COUNT = 3;

    private final AnalysisQueuePublisher queuePublisher;
    private final PythonAiClient pythonAiClient;
    private final BlogAnalysisResultRepository blogAnalysisResultRepository;
    private final BlogRecommendationRepository blogRecommendationRepository;

    /**
     * POST /blog/analyze
     * 크레딧 차감은 BC1 CreditService 완성 후 연동. 현재는 Queue 발행만 처리.
     */
    @Transactional
    public AnalyzeResponse requestAnalysis(Long userId, AnalyzeRequest request) {
        queuePublisher.publish(userId, request.documentId());
        return new AnalyzeResponse(request.documentId(), "pending", "분석이 요청되었습니다.", null);
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
     * Spring pgvector 직접 조회 (추후 구현). 현재는 빈 목록 반환.
     */
    public RecommendedCampaignResponse getRecommendations(Long userId, Long analysisId) {
        List<RecommendedCampaignResponse.CampaignItem> campaigns =
                blogRecommendationRepository.findRecommendedCampaigns(userId, analysisId, 8);
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
