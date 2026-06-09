package com.ject6.boost.presentation.blog.dto;

import jakarta.validation.constraints.NotNull;

public record AnalyzeRequest(
        @NotNull Long blogId,
        @NotNull Long documentId
) {}
