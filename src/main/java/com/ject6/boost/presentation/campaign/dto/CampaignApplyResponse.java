package com.ject6.boost.presentation.campaign.dto;

import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.domain.campaign.entity.UserCampaignApply;
import java.time.OffsetDateTime;

public record CampaignApplyResponse(
        Long id,
        Long campaignId,
        CampaignApplyStatus status,
        OffsetDateTime appliedAt
) {
    public static CampaignApplyResponse from(UserCampaignApply apply) {
        return new CampaignApplyResponse(
                apply.getId(),
                apply.getCampaignId(),
                apply.getStatus(),
                apply.getAppliedAt()
        );
    }
}
