package com.ject6.boost.presentation.my.controller;

import com.ject6.boost.application.my.service.MyService;
import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.dto.SimplePageResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.presentation.my.controller.docs.MyApi;
import com.ject6.boost.presentation.my.dto.CampaignSummaryResponse;
import com.ject6.boost.presentation.my.dto.MyAccountResponse;
import com.ject6.boost.presentation.my.dto.MyAiHistoryResponse;
import com.ject6.boost.presentation.my.dto.MyCampaignListResponse;
import com.ject6.boost.presentation.my.dto.MyCampaignSummaryResponse;
import com.ject6.boost.presentation.my.dto.MyRecentAppliedCampaignListResponse;
import com.ject6.boost.presentation.my.dto.PointBalanceResponse;
import com.ject6.boost.presentation.my.dto.PointWithdrawRequest;
import com.ject6.boost.presentation.my.dto.PointWithdrawResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
@Profile("!mock")
public class MyController implements MyApi {

    private final MyService myService;

    @GetMapping({"", "/account"})
    @Override
    public ApiResponse<MyAccountResponse> getMy(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success(myService.getMyAccount(principal.userId()));
    }

    @GetMapping("/campaigns")
    @Override
    public ApiResponse<MyCampaignSummaryResponse> getMyCampaignSummary(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success(myService.getMyCampaignSummary(principal.userId()));
    }

    @GetMapping("/campaigns/recent-views")
    @Override
    public ApiResponse<SimplePageResponse<CampaignSummaryResponse>> getRecentViewCampaigns(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PageableDefault(size = 8) Pageable pageable) {
        return ApiResponse.success(SimplePageResponse.of(myService.getRecentViews(principal.userId()), pageable));
    }

    @GetMapping("/campaigns/likes")
    @Override
    public ApiResponse<SimplePageResponse<CampaignSummaryResponse>> getLikedCampaigns(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PageableDefault(size = 8) Pageable pageable) {
        return ApiResponse.success(SimplePageResponse.of(myService.getLikes(principal.userId()), pageable));
    }

    @GetMapping("/campaigns/recent-applies")
    @Override
    public ApiResponse<SimplePageResponse<MyRecentAppliedCampaignListResponse>> getRecentApplies(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PageableDefault(size = 8) Pageable pageable) {
        return ApiResponse.success(SimplePageResponse.of(myService.getRecentApplies(principal.userId()), pageable));
    }

    @GetMapping("/ai-history")
    @Override
    public ApiResponse<MyAiHistoryResponse> getAiHistory(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(defaultValue = "3") int size) {
        return ApiResponse.success(myService.getAiHistory(principal.userId(), size));
    }

    @GetMapping("/campaigns/applies")
    @Override
    public ApiResponse<List<MyCampaignListResponse>> getMyCampaignApplies(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) CampaignApplyStatus status) {
        return ApiResponse.success(myService.getMyCampaigns(principal.userId(), status));
    }

    @GetMapping("/campaigns/{id}")
    @Override
    public ApiResponse<MyCampaignListResponse> getMyCampaignDetail(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long id) {
        return ApiResponse.success(myService.getMyCampaignDetail(principal.userId(), id));
    }

    @GetMapping("/recent-views")
    @Override
    public ApiResponse<List<CampaignSummaryResponse>> getLegacyRecentViews(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success(myService.getRecentViews(principal.userId()));
    }

    @GetMapping("/likes")
    @Override
    public ApiResponse<List<CampaignSummaryResponse>> getLegacyLikes(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success(myService.getLikes(principal.userId()));
    }

    @GetMapping("/points")
    @Override
    public ApiResponse<PointBalanceResponse> getPoints(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success(myService.getPoints(principal.userId()));
    }

    @PostMapping("/points/withdraw")
    @Override
    public ApiResponse<PointWithdrawResponse> withdraw(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody PointWithdrawRequest request) {
        return ApiResponse.success(myService.withdraw(principal.userId(), request));
    }
}
