package com.ject6.boost.presentation.my.dto;

import com.ject6.boost.domain.user.constant.CategoryType;
import com.ject6.boost.domain.user.entity.User;
import java.util.List;

public record MyAccountResponse(
        String nickname,
        String blogUrl,
        List<CategoryType> interestCategories
) {
    public static MyAccountResponse from(User user, String blogUrl) {
        return new MyAccountResponse(
                user.getNickname(),
                blogUrl,
                List.copyOf(user.getCategoryTypes())
        );
    }
}
