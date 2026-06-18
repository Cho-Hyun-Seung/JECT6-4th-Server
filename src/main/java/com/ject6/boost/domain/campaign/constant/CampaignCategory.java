package com.ject6.boost.domain.campaign.constant;

import java.util.Locale;

public enum CampaignCategory {

    FOOD,
    BEAUTY,
    FASHION,
    LIVING,
    PET,
    TECH_IT,
    TRAVEL,
    CULTURE,
    ETC;

    public static CampaignCategory from(String value) {
        return CampaignCategory.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
