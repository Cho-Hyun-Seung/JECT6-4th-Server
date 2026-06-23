package com.ject6.boost.presentation.my.dto;

import java.util.List;

public record MyCampaignSummaryResponse(
        long recentViewCount,
        long likedCount,
        List<MyRecentAppliedCampaignSummaryResponse> recentAppliedCampaign
) {
}
