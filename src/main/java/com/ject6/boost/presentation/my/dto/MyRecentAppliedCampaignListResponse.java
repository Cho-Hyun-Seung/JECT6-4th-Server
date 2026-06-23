package com.ject6.boost.presentation.my.dto;

import com.ject6.boost.domain.campaign.entity.Campaign;
import com.ject6.boost.domain.campaign.entity.UserCampaignApply;
import java.time.LocalDate;

public record MyRecentAppliedCampaignListResponse(
        Long id,
        Long campaignId,
        String campaignTitle,
        String brandName,
        LocalDate appliedAt,
        LocalDate applyEndDate
) {
    public static MyRecentAppliedCampaignListResponse from(UserCampaignApply apply, Campaign campaign) {
        return new MyRecentAppliedCampaignListResponse(
                apply.getId(),
                apply.getCampaignId(),
                campaign != null ? campaign.getTitle() : "삭제된 공고",
                campaign != null ? campaign.getBrandName() : "",
                apply.getAppliedAt() != null ? apply.getAppliedAt().toLocalDate() : null,
                campaign != null ? campaign.getApplyEndDate() : null
        );
    }
}
