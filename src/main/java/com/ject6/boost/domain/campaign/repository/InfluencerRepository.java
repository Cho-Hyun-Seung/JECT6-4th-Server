package com.ject6.boost.domain.campaign.repository;

import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.entity.Influencer;
import java.util.List;

public interface InfluencerRepository {

    List<Influencer> findFeedStories(CampaignCategory category);
}
