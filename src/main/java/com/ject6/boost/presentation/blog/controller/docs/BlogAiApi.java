package com.ject6.boost.presentation.blog.controller.docs;

import com.ject6.boost.presentation.blog.dto.AnalyzeRequest;
import com.ject6.boost.presentation.blog.dto.AnalyzeResponse;
import com.ject6.boost.presentation.blog.dto.BlogAnalysisDetailResponse;
import com.ject6.boost.presentation.blog.dto.BlogAnalysisHistoryResponse;
import com.ject6.boost.presentation.blog.dto.BloggerResponse;
import com.ject6.boost.presentation.blog.dto.ChatRequest;
import com.ject6.boost.presentation.blog.dto.ChatResponse;
import com.ject6.boost.presentation.blog.dto.RecommendedCampaignResponse;
import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "블로그 AI", description = "블로그 AI 분석 및 챗봇 API")
public interface BlogAiApi {

    @Operation(summary = "블로그 분석 요청", description = "크레딧 차감 후 분석 큐에 등록합니다. analysisMode 허용값: FULL_BLOG, POST")
    ApiResponse<AnalyzeResponse> analyze(AuthenticatedUser principal, AnalyzeRequest request);

    @Operation(summary = "분석 결과 조회", description = "분석 상태는 PENDING, IN_PROGRESS, COMPLETED, FAILED 중 하나입니다.")
    ApiResponse<BlogAnalysisDetailResponse> getAnalysis(AuthenticatedUser principal, Long documentId);

    @Operation(summary = "분석 이력 조회", description = "무료 사용자는 최근 3건, 프리미엄 사용자는 전체 이력을 조회할 수 있습니다.")
    ApiResponse<BlogAnalysisHistoryResponse> getHistory(AuthenticatedUser principal);

    @Operation(summary = "AI 추천 공고", description = "분석 결과를 기반으로 유사도가 높은 공고를 최대 8개 추천합니다.")
    ApiResponse<RecommendedCampaignResponse> getRecommendations(AuthenticatedUser principal, Long analysisId);

    @Operation(summary = "인기 블로거 Top3", description = "카테고리별 인기 블로거를 조회합니다.")
    ApiResponse<BloggerResponse> getBloggers(AuthenticatedUser principal, Long analysisId);

    @Operation(summary = "AI 챗봇 메시지", description = "분석 결과를 기반으로 AI 챗봇과 대화합니다.")
    ApiResponse<ChatResponse> chat(AuthenticatedUser principal, ChatRequest request);

    @Operation(summary = "챗봇 세션 초기화", description = "지정한 챗봇 세션을 초기화합니다.")
    ApiResponse<Void> resetSession(AuthenticatedUser principal, String sessionId);
}
