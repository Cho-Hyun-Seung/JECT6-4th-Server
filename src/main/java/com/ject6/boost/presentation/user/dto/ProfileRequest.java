package com.ject6.boost.presentation.user.dto;

import java.util.List;

public record ProfileRequest(
        String nickname,
        List<String> categoryTypes,
        List<String> activityTypes,
        List<Long> regionIds
) {
}
