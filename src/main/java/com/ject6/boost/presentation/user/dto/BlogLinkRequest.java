package com.ject6.boost.presentation.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BlogLinkRequest(
        @JsonProperty("blog_url")
        String blogUrl,
        String platform
) {
}
