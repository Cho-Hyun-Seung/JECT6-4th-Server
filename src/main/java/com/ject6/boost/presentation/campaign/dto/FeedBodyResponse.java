package com.ject6.boost.presentation.campaign.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedBodyResponse {

    private List<CampaignListResponse> popular;
    private List<CampaignListResponse> closingSoon;
    private List<CampaignListResponse> guaranteed;

    public static FeedBodyResponse of(
        List<CampaignListResponse> popular,
        List<CampaignListResponse> closingSoon,
        List<CampaignListResponse> guaranteed) {
        return FeedBodyResponse.builder()
            .popular(popular)
            .closingSoon(closingSoon)
            .guaranteed(guaranteed)
            .build();
    }
}
