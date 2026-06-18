package com.ject6.boost.presentation.campaign.dto;

import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.constant.CampaignType;
import com.ject6.boost.domain.campaign.constant.SortType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignFilterRequest {
    private List<CampaignCategory> categories;
    private CampaignCategory category;
    private CampaignType type;
    private String region;
    private String sourcePlatform;
    private String channel;
    private SortType sort;

    public List<CampaignCategory> getCategories() {
        if (categories != null && !categories.isEmpty()) {
            return categories;
        }
        if (category != null) {
            return List.of(category);
        }
        return null;
    }
}
