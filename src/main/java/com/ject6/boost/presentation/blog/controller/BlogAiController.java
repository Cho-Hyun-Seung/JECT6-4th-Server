package com.ject6.boost.presentation.blog.controller;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.application.blog.service.BlogAiService;
import com.ject6.boost.presentation.blog.controller.docs.BlogAiApi;
import com.ject6.boost.presentation.blog.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
@Profile("!mock")
public class BlogAiController implements BlogAiApi {

    private final BlogAiService blogAiService;

    @PostMapping("/analyze")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Override
    public ApiResponse<AnalyzeResponse> analyze(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody AnalyzeRequest request) {
        return ApiResponse.success(blogAiService.requestAnalysis(principal.userId(), request));
    }

    @GetMapping("/analysis/{documentId}")
    @Override
    public ApiResponse<BlogAnalysisDetailResponse> getAnalysis(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long documentId) {
        return ApiResponse.success(blogAiService.getAnalysis(documentId));
    }

    @GetMapping("/analysis/history")
    @Override
    public ApiResponse<BlogAnalysisHistoryResponse> getHistory(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        // isPremium: BC1 D03 구독 완성 후 연동. 현재는 false(Free) 기본값.
        return ApiResponse.success(blogAiService.getHistory(principal.userId(), false));
    }

    @GetMapping("/analysis/{analysisId}/recommendations")
    @Override
    public ApiResponse<RecommendedCampaignResponse> getRecommendations(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long analysisId) {
        return ApiResponse.success(blogAiService.getRecommendations(principal.userId(), analysisId));
    }

    @GetMapping("/analysis/{analysisId}/bloggers")
    @Override
    public ApiResponse<BloggerResponse> getBloggers(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long analysisId) {
        return ApiResponse.success(blogAiService.getBloggers(principal.userId(), analysisId));
    }

    @PostMapping("/chat")
    @Override
    public ApiResponse<ChatResponse> chat(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody ChatRequest request) {
        return ApiResponse.success(blogAiService.chat(principal.userId(), request));
    }

    @DeleteMapping("/chat/{sessionId}")
    @Override
    public ApiResponse<Void> resetSession(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable String sessionId) {
        blogAiService.resetSession(sessionId);
        return ApiResponse.success(null);
    }
}
