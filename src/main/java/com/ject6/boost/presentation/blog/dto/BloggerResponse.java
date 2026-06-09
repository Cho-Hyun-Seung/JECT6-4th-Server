package com.ject6.boost.presentation.blog.dto;

import java.util.List;

public record BloggerResponse(
        String category,
        List<BloggerItem> bloggers
) {
    public record BloggerItem(
            String nickname,
            int overallScore,
            String profileUrl
    ) {}
}
