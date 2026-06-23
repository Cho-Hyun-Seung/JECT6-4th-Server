package com.ject6.boost.presentation.campaign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record CampaignBulkRequest(
        @Schema(description = "저장할 공고 목록")
        List<Item> campaigns
) {

    public record Item(
            @Schema(description = "원본 플랫폼")
            String sourcePlatform,
            @Schema(description = "브랜드명")
            String brandName,
            @Schema(description = "공고 제목")
            String title,
            @Schema(description = "썸네일 URL")
            String thumbnailUrl,
            @Schema(
                    description = "공고 카테고리. 허용값: FOOD, BEAUTY, FASHION, LIVING, PET, TECH_IT, TRAVEL, CULTURE, ETC",
                    allowableValues = {"FOOD", "BEAUTY", "FASHION", "LIVING", "PET", "TECH_IT", "TRAVEL", "CULTURE", "ETC"}
            )
            String category,
            @Schema(
                    description = "공고 유형. 허용값: VISIT, DELIVERY, REPORTER, REVIEW, PAYBACK",
                    allowableValues = {"VISIT", "DELIVERY", "REPORTER", "REVIEW", "PAYBACK"}
            )
            String type,
            @Schema(description = "채널. 예: BLOG, INSTAGRAM, YOUTUBE, TIKTOK")
            String channel,
            @Schema(description = "지역명")
            String region,
            @Schema(description = "상위 지역 ID")
            Long parentRegionId,
            @Schema(description = "하위 지역 ID")
            Long childRegionId,
            @Schema(description = "제공 내역")
            String providedContent,
            @Schema(description = "모집 인원")
            Integer recruitCount,
            @Schema(description = "지원 시작일")
            String applyStartDate,
            @Schema(description = "지원 마감일")
            String applyEndDate,
            @Schema(description = "미션")
            String mission,
            @Schema(description = "원본 공고 URL")
            String sourceUrl,
            @Schema(description = "100% 당첨 여부")
            Boolean isGuaranteed
    ) {}
}
