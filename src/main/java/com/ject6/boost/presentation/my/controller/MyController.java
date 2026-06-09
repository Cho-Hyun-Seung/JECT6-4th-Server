package com.ject6.boost.presentation.my.controller;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.domain.campaign.constant.UserCampaignStatus;
import com.ject6.boost.application.my.service.MyService;
import com.ject6.boost.presentation.my.controller.docs.MyApi;
import com.ject6.boost.presentation.my.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyController implements MyApi {

    private final MyService myService;

    @GetMapping("/campaigns")
    @Override
    public ApiResponse<List<MyCampaignListResponse>> getMyCampaigns(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) UserCampaignStatus status) {
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
    public ApiResponse<List<CampaignSummaryResponse>> getRecentViews(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success(myService.getRecentViews(principal.userId()));
    }

    @GetMapping("/likes")
    @Override
    public ApiResponse<List<CampaignSummaryResponse>> getLikes(
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
