package com.ject6.boost.presentation.my.dto;

import com.ject6.boost.domain.campaign.constant.UserCampaignStatus;
import com.ject6.boost.domain.campaign.entity.UserCampaign;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public record MyCampaignListResponse(
        Long id,
        Long campaignId,
        String campaignTitle,
        String brandName,
        UserCampaignStatus status,
        OffsetDateTime appliedAt,
        LocalDate reviewDeadline,
        int dDay,
        Integer rewardAmount,
        boolean isUrgent
) {
    public static MyCampaignListResponse from(UserCampaign uc, String title, String brandName) {
        int dDay = uc.getReviewDeadline() != null
                ? (int) ChronoUnit.DAYS.between(LocalDate.now(), uc.getReviewDeadline())
                : -1;
        return new MyCampaignListResponse(
                uc.getId(), uc.getCampaignId(), title, brandName,
                uc.getStatus(), uc.getAppliedAt(), uc.getReviewDeadline(),
                Math.max(dDay, 0), uc.getRewardAmount(), dDay >= 0 && dDay <= 3
        );
    }
}
