package com.ject6.boost.presentation.campaign.dto;

import java.util.List;

public record CampaignBulkRequest(List<Item> campaigns) {

    public record Item(
            String sourcePlatform,
            String brandName,
            String title,
            String thumbnailUrl,
            String category,
            String type,
            String channel,
            String region,
            Long parentRegionId,
            Long childRegionId,
            String providedContent,
            Integer recruitCount,
            String applyStartDate,
            String applyEndDate,
            String mission,
            String sourceUrl,
            Boolean isGuaranteed
    ) {}
}
