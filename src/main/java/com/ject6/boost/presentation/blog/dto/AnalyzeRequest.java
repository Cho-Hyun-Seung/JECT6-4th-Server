package com.ject6.boost.presentation.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record AnalyzeRequest(
        @Schema(description = "분석할 블로그 ID")
        Long blogId,
        @Schema(description = "분석 문서 ID")
        Long documentId,
        @Schema(
                description = "분석 모드. 허용값: FULL_BLOG, POST",
                allowableValues = {"FULL_BLOG", "POST"},
                example = "FULL_BLOG"
        )
        @Pattern(regexp = "FULL_BLOG|POST|full_blog|post",
                message = "analysisMode는 FULL_BLOG 또는 POST 중 하나여야 합니다.")
        String analysisMode
) {}
