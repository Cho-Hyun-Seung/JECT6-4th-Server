package com.ject6.boost.presentation.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record BlogLinkRequest(
        @JsonProperty("blog_url")
        @Schema(description = "연동할 블로그 URL. https:// 로 시작해야 합니다.")
        String blogUrl,
        @Schema(description = "블로그 플랫폼. 허용값: NAVER", allowableValues = {"NAVER"})
        String platform
) {
}
