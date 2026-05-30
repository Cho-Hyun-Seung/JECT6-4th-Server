package com.ject6.boost.domain.auth.application.dto;

public record OAuthLoginRequest(
        String code,
        String redirectUri,
        String state
) {
}
