package com.ject6.boost.presentation.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthLoginRequest(
        String code,
        @JsonProperty("redirect_uri")
        String redirectUri
) {
}
