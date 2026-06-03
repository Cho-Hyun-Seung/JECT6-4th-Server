package com.ject6.boost.domain.user.presentation.dto;

import java.util.List;

public record OnboardingProfileRequest(
        String nickname,
        List<String> categoryTypes,
        List<String> activityTypes,
        List<Long> regionIds
) {
}
