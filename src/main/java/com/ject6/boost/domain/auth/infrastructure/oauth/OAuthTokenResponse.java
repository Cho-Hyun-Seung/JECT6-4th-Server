package com.ject6.boost.domain.auth.infrastructure.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OAuthTokenResponse(
        String tokenType,
        String accessToken,
        Long expiresIn,
        String refreshToken,
        String scope
) {
}
