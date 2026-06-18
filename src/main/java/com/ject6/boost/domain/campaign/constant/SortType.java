package com.ject6.boost.domain.campaign.constant;

import java.util.Locale;

public enum SortType {
    CLOSING,
    COMPETITION,
    POPULAR;

    public static SortType from(String value) {
        return SortType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
