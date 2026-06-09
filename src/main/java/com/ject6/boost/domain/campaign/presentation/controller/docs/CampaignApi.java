package com.ject6.boost.domain.campaign.presentation.controller.docs;

import com.ject6.boost.common.dto.ApiResponse;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignDetailResponse;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignFilterRequest;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Tag(name = "Campaign", description = "공고 API")
public interface CampaignApi {

    @Operation(summary = "공고 목록 조회", description = "필터/정렬/페이지네이션 적용")
    ResponseEntity<ApiResponse<Page<CampaignListResponse>>> getCampaigns(
        CampaignFilterRequest filter, Pageable pageable);

    @Operation(summary = "공고 상세 조회")
    ResponseEntity<ApiResponse<CampaignDetailResponse>> getCampaign(
        @PathVariable Long id);

    @Operation(summary = "실시간 조회자 수 조회")
    ResponseEntity<ApiResponse<Map<String, Long>>> getViewers(
        @PathVariable Long id);

    @Operation(summary = "관련 공고 조회", description = "동일 카테고리 3개")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getRelated(
        @PathVariable Long id);

    @Operation(summary = "통합 검색", description = "공고명·브랜드명 키워드 검색")
    ResponseEntity<ApiResponse<Page<CampaignListResponse>>> search(
        @RequestParam String keyword, Pageable pageable);

    @Operation(summary = "인기 체험단 목록")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getPopular();

    @Operation(summary = "100% 당첨 공고 목록")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getGuaranteed();

    @Operation(summary = "마감 임박 공고 목록")
    ResponseEntity<ApiResponse<List<CampaignListResponse>>> getClosingSoon();
}
