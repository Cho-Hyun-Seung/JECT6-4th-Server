package com.ject6.boost.presentation.my.controller.docs;

import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.dto.SimplePageResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.presentation.my.dto.CampaignSummaryResponse;
import com.ject6.boost.presentation.my.dto.MyAccountResponse;
import com.ject6.boost.presentation.my.dto.MyAiHistoryResponse;
import com.ject6.boost.presentation.my.dto.MyCampaignListResponse;
import com.ject6.boost.presentation.my.dto.MyCampaignSummaryResponse;
import com.ject6.boost.presentation.my.dto.MyRecentAppliedCampaignListResponse;
import com.ject6.boost.presentation.my.dto.PointBalanceResponse;
import com.ject6.boost.presentation.my.dto.PointWithdrawRequest;
import com.ject6.boost.presentation.my.dto.PointWithdrawResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Tag(name = "마이페이지", description = "마이페이지 API")
public interface MyApi {

    @Operation(summary = "내 계정 조회", description = "닉네임, 블로그 URL, 관심 카테고리를 조회합니다.")
    ApiResponse<MyAccountResponse> getMy(AuthenticatedUser principal);

    @Operation(summary = "내 관심 공고 요약 조회", description = "최근 조회 공고 수, 좋아요 공고 수, 최근 지원 공고를 조회합니다.")
    ApiResponse<MyCampaignSummaryResponse> getMyCampaignSummary(AuthenticatedUser principal);

    @Operation(summary = "최근 조회 공고 목록 조회", description = "최근 조회한 공고를 페이지당 기본 8개씩 조회합니다.")
    ApiResponse<SimplePageResponse<CampaignSummaryResponse>> getRecentViewCampaigns(
            AuthenticatedUser principal,
            Pageable pageable);

    @Operation(summary = "좋아요한 공고 목록 조회", description = "좋아요한 공고를 페이지당 기본 8개씩 조회합니다.")
    ApiResponse<SimplePageResponse<CampaignSummaryResponse>> getLikedCampaigns(
            AuthenticatedUser principal,
            Pageable pageable);

    @Operation(summary = "최근 지원한 공고 목록 조회", description = "최근 지원한 공고를 페이지당 기본 8개씩 조회합니다.")
    ApiResponse<SimplePageResponse<MyRecentAppliedCampaignListResponse>> getRecentApplies(
            AuthenticatedUser principal,
            Pageable pageable);

    @Operation(summary = "AI 히스토리 조회", description = "최근 AI 진단 히스토리를 조회합니다. historyId는 상세 조회에 사용하고, diagnosisDate는 YYYYMMDD 형식입니다.")
    ApiResponse<MyAiHistoryResponse> getAiHistory(AuthenticatedUser principal, int size);

    @Operation(summary = "내 체험단 지원 목록", description = "기존 지원 목록 호환 엔드포인트입니다.")
    ApiResponse<List<MyCampaignListResponse>> getMyCampaignApplies(
            AuthenticatedUser principal,
            @Parameter(
                    description = "지원 상태 필터. 허용값: APPLIED, REVIEWING, SELECTED, COMPLETED",
                    schema = @Schema(allowableValues = {"APPLIED", "REVIEWING", "SELECTED", "COMPLETED"})
            )
            CampaignApplyStatus status);

    @Operation(summary = "내 체험단 상세")
    ApiResponse<MyCampaignListResponse> getMyCampaignDetail(AuthenticatedUser principal, Long id);

    @Operation(summary = "최근 본 공고", description = "기존 화면 호환 엔드포인트입니다.")
    ApiResponse<List<CampaignSummaryResponse>> getLegacyRecentViews(AuthenticatedUser principal);

    @Operation(summary = "찜한 공고 목록", description = "기존 화면 호환 엔드포인트입니다.")
    ApiResponse<List<CampaignSummaryResponse>> getLegacyLikes(AuthenticatedUser principal);

    @Operation(summary = "포인트 잔액 및 거래 내역")
    ApiResponse<PointBalanceResponse> getPoints(AuthenticatedUser principal);

    @Operation(summary = "포인트 출금 신청", description = "최소 5,000P부터 출금을 신청할 수 있습니다.")
    ApiResponse<PointWithdrawResponse> withdraw(AuthenticatedUser principal, PointWithdrawRequest request);
}
