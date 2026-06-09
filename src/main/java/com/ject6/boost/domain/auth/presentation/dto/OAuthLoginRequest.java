package com.ject6.boost.domain.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthLoginRequest(
        String code,
        @JsonProperty("redirect_uri")
        String redirectUri
) {
}
