package com.ject6.boost.domain.auth.application.dto;

public record OAuthLoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        OAuthUserProfile user
) {
}
