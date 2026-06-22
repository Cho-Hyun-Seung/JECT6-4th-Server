package com.ject6.boost.presentation.my.controller.docs;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.presentation.my.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "My", description = "마이페이지 API")
public interface MyApi {

    @Operation(summary = "내 체험단 목록", description = "status 필터: APPLIED|REVIEWING|SELECTED|COMPLETED (생략 시 전체)")
    ApiResponse<List<MyCampaignListResponse>> getMyCampaigns(AuthenticatedUser principal, CampaignApplyStatus status);

    @Operation(summary = "내 체험단 상세")
    ApiResponse<MyCampaignListResponse> getMyCampaignDetail(AuthenticatedUser principal, Long id);

    @Operation(summary = "최근 본 공고 (최대 20개)")
    ApiResponse<List<CampaignSummaryResponse>> getRecentViews(AuthenticatedUser principal);

    @Operation(summary = "찜한 공고 목록")
    ApiResponse<List<CampaignSummaryResponse>> getLikes(AuthenticatedUser principal);

    @Operation(summary = "포인트 잔액 및 거래 내역")
    ApiResponse<PointBalanceResponse> getPoints(AuthenticatedUser principal);

    @Operation(summary = "포인트 출금 신청 (최소 5,000P)")
    ApiResponse<PointWithdrawResponse> withdraw(AuthenticatedUser principal, PointWithdrawRequest request);
}
