package com.ject6.boost.domain.user.presentation.dto;

import com.ject6.boost.domain.user.domain.constant.ActivityType;
import com.ject6.boost.domain.user.domain.constant.CategoryType;
import java.util.List;

public record ProfileResponse(
        Long userId,
        String nickname,
        boolean profileCompleted,
        List<CategoryType> categoryTypes,
        List<ActivityType> activityTypes,
        List<Long> regionIds
) {
}
