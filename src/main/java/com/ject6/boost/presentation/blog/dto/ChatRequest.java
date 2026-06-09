package com.ject6.boost.presentation.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatRequest(
        @NotBlank String sessionId,
        @NotNull Long documentId,
        @NotBlank String message
) {}
