package com.ject6.boost.domain.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthLoginResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("expires_in")
        long expiresIn,
        @JsonProperty("token_type")
        String tokenType,
        OAuthLoginUserResponse user
) {
}
