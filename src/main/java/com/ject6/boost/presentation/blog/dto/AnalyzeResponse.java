package com.ject6.boost.presentation.blog.dto;

public record AnalyzeResponse(
        Long documentId,
        String status,
        String message,
        Integer aiCreditRemaining
) {}
