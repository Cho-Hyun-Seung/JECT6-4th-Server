package com.ject6.boost.domain.my.presentation.dto;

import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import java.time.LocalDate;

public record CampaignSummaryResponse(
        Long id,
        String title,
        String brandName,
        String category,
        String thumbnailUrl,
        LocalDate applyEndDate,
        Integer rewardAmount
) {
    public static CampaignSummaryResponse from(Campaign campaign) {
        return new CampaignSummaryResponse(
                campaign.getId(), campaign.getTitle(), campaign.getBrandName(),
                campaign.getCategory() != null ? campaign.getCategory().name() : null,
                campaign.getThumbnailUrl(),
                campaign.getApplyEndDate(), null
        );
    }
}
