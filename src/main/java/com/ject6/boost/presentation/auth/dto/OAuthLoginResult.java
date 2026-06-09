package com.ject6.boost.presentation.auth.dto;

public record OAuthLoginResult(
        OAuthLoginResponse response,
        String refreshToken,
        long refreshTokenExpiresIn
) {
}
