package com.ject6.boost.presentation.auth.dto;

public record TokenRefreshResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
