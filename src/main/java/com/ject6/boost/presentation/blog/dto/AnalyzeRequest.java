package com.ject6.boost.presentation.blog.dto;

import jakarta.validation.constraints.Pattern;

public record AnalyzeRequest(
        Long blogId,
        Long documentId,
        @Pattern(regexp = "FULL_BLOG|POST|full_blog|post",
                 message = "analysisMode는 FULL_BLOG 또는 POST 중 하나여야 합니다.")
        String analysisMode
) {}
