package com.ject6.boost.domain.user.application.dto;

import java.util.List;

public record OnboardingProfileRequest(
        String nickname,
        List<Long> categoryIds,
        List<String> activityTypes,
        List<Long> regionIds
) {
}
