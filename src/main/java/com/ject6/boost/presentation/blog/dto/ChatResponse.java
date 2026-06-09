package com.ject6.boost.presentation.blog.dto;

public record ChatResponse(
        String sessionId,
        String reply,
        int tokensUsed,
        int tokensRemaining
) {}
