package com.ject6.boost.domain.user.presentation.dto;

import com.ject6.boost.domain.user.domain.constant.ActivityType;
import com.ject6.boost.domain.user.domain.constant.CategoryType;
import java.util.List;

public record OnboardingProfileResponse(
        Long userId,
        String nickname,
        boolean onboardingCompleted,
        List<CategoryType> categoryTypes,
        List<ActivityType> activityTypes,
        List<Long> regionIds
) {
}
