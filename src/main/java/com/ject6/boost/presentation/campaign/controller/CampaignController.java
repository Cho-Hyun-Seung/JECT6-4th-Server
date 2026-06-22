package com.ject6.boost.presentation.campaign.controller;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.application.campaign.service.CampaignApplyService;
import com.ject6.boost.application.campaign.service.CampaignLikeService;
import com.ject6.boost.application.campaign.service.CampaignSearchService;
import com.ject6.boost.application.campaign.service.CampaignService;
import com.ject6.boost.presentation.campaign.controller.docs.CampaignApi;
import com.ject6.boost.presentation.campaign.dto.CampaignApplyResponse;
import com.ject6.boost.presentation.campaign.dto.CampaignDetailResponse;
import com.ject6.boost.presentation.campaign.dto.CampaignFilterRequest;
import com.ject6.boost.presentation.campaign.dto.CampaignListResponse;
import com.ject6.boost.presentation.campaign.dto.LikeAnalysisResponse;
import com.ject6.boost.presentation.campaign.dto.LikeToggleResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController implements CampaignApi {

    private final CampaignService campaignService;
    private final CampaignSearchService campaignSearchService;
    private final CampaignLikeService campaignLikeService;
    private final CampaignApplyService campaignApplyService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CampaignListResponse>>> getCampaigns(
        CampaignFilterRequest filter,
        Pageable pageable,
        @AuthenticationPrincipal AuthenticatedUser auth) {
        return ResponseEntity.ok(ApiResponse.success(
            campaignService.getCampaigns(filter, pageable, userIdOrNull(auth))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> getCampaign(
        @PathVariable Long id,
        @AuthenticationPrincipal AuthenticatedUser auth) {
        return ResponseEntity.ok(ApiResponse.success(
            campaignService.getCampaign(id, userIdOrNull(auth))));
    }

    @GetMapping("/{id}/viewers")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getViewers(
        @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
            Map.of("count", campaignService.getViewerCount(id))));
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<CampaignListResponse>>> getRelated(
        @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
            campaignService.getRelated(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CampaignListResponse>>> search(
        @RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
            campaignSearchService.search(keyword, pageable)));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<CampaignListResponse>>> getPopular() {
        return ResponseEntity.ok(ApiResponse.success(
            campaignService.getPopular()));
    }

    @GetMapping("/guaranteed")
    public ResponseEntity<ApiResponse<List<CampaignListResponse>>> getGuaranteed() {
        return ResponseEntity.ok(ApiResponse.success(
            campaignService.getGuaranteed()));
    }

    @GetMapping("/closing-soon")
    public ResponseEntity<ApiResponse<List<CampaignListResponse>>> getClosingSoon() {
        return ResponseEntity.ok(ApiResponse.success(
            campaignService.getClosingSoon()));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<LikeToggleResponse>> toggleLike(
        @PathVariable Long id,
        @AuthenticationPrincipal AuthenticatedUser auth) {
        return ResponseEntity.ok(ApiResponse.success(
            campaignLikeService.toggleLike(auth.userId(), id)));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<ApiResponse<CampaignApplyResponse>> apply(
        @PathVariable Long id,
        @AuthenticationPrincipal AuthenticatedUser auth) {
        return ResponseEntity.ok(ApiResponse.success(
            campaignApplyService.apply(auth.userId(), id)));
    }

    @GetMapping("/{id}/likes/analysis")
    public ResponseEntity<ApiResponse<LikeAnalysisResponse>> getLikeAnalysis(
        @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
            campaignLikeService.getLikeAnalysis(id)));
    }

    private Long userIdOrNull(AuthenticatedUser auth) {
        return auth == null ? null : auth.userId();
    }
}
