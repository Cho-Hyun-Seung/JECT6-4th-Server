package com.ject6.boost.presentation.blog.controller.docs;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.presentation.blog.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Blog AI", description = "블로그 AI 분석 · 챗봇 API")
public interface BlogAiApi {

    @Operation(summary = "블로그 분석 요청", description = "크레딧 차감 후 Queue 발행. 202 Accepted 비동기.")
    ApiResponse<AnalyzeResponse> analyze(AuthenticatedUser principal, AnalyzeRequest request);

    @Operation(summary = "분석 결과 조회", description = "status: pending|in_progress|completed|failed")
    ApiResponse<BlogAnalysisDetailResponse> getAnalysis(AuthenticatedUser principal, Long documentId);

    @Operation(summary = "분석 이력 조회", description = "Free: 최근 3건 열람 가능. Premium: 전체")
    ApiResponse<BlogAnalysisHistoryResponse> getHistory(AuthenticatedUser principal);

    @Operation(summary = "AI 추천 공고", description = "분석 기반 pgvector 유사도 추천 (최대 8개)")
    ApiResponse<RecommendedCampaignResponse> getRecommendations(AuthenticatedUser principal, Long analysisId);

    @Operation(summary = "인기 블로거 Top3", description = "카테고리별 인기 블로거 조회")
    ApiResponse<BloggerResponse> getBloggers(AuthenticatedUser principal, Long analysisId);

    @Operation(summary = "AI 챗봇 메시지", description = "분석 결과 기반 대화. Python HTTP 동기 프록시.")
    ApiResponse<ChatResponse> chat(AuthenticatedUser principal, ChatRequest request);

    @Operation(summary = "챗봇 세션 초기화")
    ApiResponse<Void> resetSession(AuthenticatedUser principal, String sessionId);
}
