package com.ject6.boost.presentation.campaign.controller.docs;

import com.ject6.boost.presentation.campaign.dto.CampaignApplyResponse;
import com.ject6.boost.presentation.campaign.dto.CampaignDetailResponse;
import com.ject6.boost.presentation.campaign.dto.CampaignFilterRequest;
import com.ject6.boost.presentation.campaign.dto.CampaignListResponse;
import com.ject6.boost.presentation.campaign.dto.LikeAnalysisResponse;
import com.ject6.boost.presentation.campaign.dto.LikeToggleResponse;
import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "공고", description = "체험단 공고 API")
public interface CampaignApi {

    @Operation(
            summary = "공고 목록 조회",
            description = "필터, 정렬, 페이지 조건을 적용해 공고 목록을 조회합니다. "
                    + "categories/category 허용값: FOOD, BEAUTY, FASHION, LIVING, PET, TECH_IT, TRAVEL, CULTURE, ETC. "
                    + "sort 허용값: CLOSING, COMPETITION, POPULAR. 예: sort=CLOSING,asc"
    )
    ResponseEntity<ApiResponse<Page<CampaignListResponse>>> getCampaigns(
            CampaignFilterRequest filter,
            Pageable pageable,
            @AuthenticationPrincipal AuthenticatedUser auth);

    @Operation(summary = "공고 상세 조회")
    ResponseEntity<ApiResponse<CampaignDetailResponse>> getCampaign(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser auth);

    @Operation(summary = "실시간 조회자 수 조회")
    ResponseEntity<ApiResponse<Map<String, Long>>> getViewers(
            @PathVariable Long id);

    @Operation(summary = "관련 공고 조회", description = "같은 카테고리의 관련 공고를 최대 3개까지 조회합니다.")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getRelated(
            @PathVariable Long id);

    @Operation(summary = "공고 검색", description = "공고 제목과 브랜드명을 기준으로 검색합니다.")
    ResponseEntity<ApiResponse<Page<CampaignListResponse>>> search(
            @Parameter(description = "검색어")
            @RequestParam String keyword,
            Pageable pageable);

    @Operation(summary = "인기 공고 조회")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getPopular();

    @Operation(summary = "100% 당첨 공고 조회")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getGuaranteed();

    @Operation(summary = "마감 임박 공고 조회")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getClosingSoon();

    @Operation(summary = "공고 찜 토글", description = "인증된 사용자의 공고 찜을 추가하거나 취소합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    ResponseEntity<ApiResponse<LikeToggleResponse>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser auth);

    @Operation(summary = "공고 지원", description = "인증된 사용자의 공고 지원 내역을 생성합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    ResponseEntity<ApiResponse<CampaignApplyResponse>> apply(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser auth);

    @Operation(summary = "찜 분석 조회")
    ResponseEntity<ApiResponse<LikeAnalysisResponse>> getLikeAnalysis(
            @PathVariable Long id);
}
