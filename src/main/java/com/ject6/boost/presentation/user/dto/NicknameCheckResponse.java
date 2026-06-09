package com.ject6.boost.presentation.user.dto;

public record NicknameCheckResponse(
        String nickname,
        boolean available
) {
}
