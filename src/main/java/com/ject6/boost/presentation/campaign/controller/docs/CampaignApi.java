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

@Tag(name = "Campaign", description = "Campaign API")
public interface CampaignApi {

    @Operation(summary = "List campaigns", description = "Applies filters, sorting, and pagination.")
    ResponseEntity<ApiResponse<Page<CampaignListResponse>>> getCampaigns(
            CampaignFilterRequest filter,
            Pageable pageable,
            @AuthenticationPrincipal AuthenticatedUser auth);

    @Operation(summary = "Get campaign detail")
    ResponseEntity<ApiResponse<CampaignDetailResponse>> getCampaign(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser auth);

    @Operation(summary = "Get live viewer count")
    ResponseEntity<ApiResponse<Map<String, Long>>> getViewers(
            @PathVariable Long id);

    @Operation(summary = "Get related campaigns", description = "Returns up to 3 campaigns in the same category.")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getRelated(
            @PathVariable Long id);

    @Operation(summary = "Search campaigns", description = "Searches campaign title and brand name.")
    ResponseEntity<ApiResponse<Page<CampaignListResponse>>> search(
            @RequestParam String keyword,
            Pageable pageable);

    @Operation(summary = "Get popular campaigns")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getPopular();

    @Operation(summary = "Get guaranteed campaigns")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getGuaranteed();

    @Operation(summary = "Get closing-soon campaigns")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getClosingSoon();

    @Operation(summary = "Toggle campaign like", description = "Adds or removes the authenticated user's like.",
            security = @SecurityRequirement(name = "bearerAuth"))
    ResponseEntity<ApiResponse<LikeToggleResponse>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser auth);

    @Operation(summary = "Apply to campaign", description = "Creates an application for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth"))
    ResponseEntity<ApiResponse<CampaignApplyResponse>> apply(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser auth);

    @Operation(summary = "Get like analysis")
    ResponseEntity<ApiResponse<LikeAnalysisResponse>> getLikeAnalysis(
            @PathVariable Long id);
}
