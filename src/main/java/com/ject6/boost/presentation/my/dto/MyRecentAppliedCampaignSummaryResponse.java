package com.ject6.boost.presentation.my.dto;

import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.domain.campaign.entity.Campaign;
import com.ject6.boost.domain.campaign.entity.UserCampaignApply;
import java.time.LocalDate;

public record MyRecentAppliedCampaignSummaryResponse(
        Long id,
        Long campaignId,
        String title,
        String brandName,
        CampaignApplyStatus status,
        LocalDate appliedAt,
        LocalDate applyEndDate
) {
    public static MyRecentAppliedCampaignSummaryResponse from(UserCampaignApply apply, Campaign campaign) {
        return new MyRecentAppliedCampaignSummaryResponse(
                apply.getId(),
                apply.getCampaignId(),
                campaign != null ? campaign.getTitle() : "삭제된 공고",
                campaign != null ? campaign.getBrandName() : "",
                apply.getStatus(),
                apply.getAppliedAt() != null ? apply.getAppliedAt().toLocalDate() : null,
                campaign != null ? campaign.getApplyEndDate() : null
        );
    }
}
