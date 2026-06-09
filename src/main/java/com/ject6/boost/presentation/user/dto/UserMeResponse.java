package com.ject6.boost.presentation.user.dto;

import com.ject6.boost.domain.user.constant.ActivityType;
import com.ject6.boost.domain.user.constant.CategoryType;
import java.util.List;

public record UserMeResponse(
        Long id,
        String nickname,
        boolean profileCompleted,
        List<CategoryType> categoryTypes,
        List<ActivityType> activityTypes,
        List<Long> regionIds,
        List<BlogLinkResponse> blogs
) {
}
