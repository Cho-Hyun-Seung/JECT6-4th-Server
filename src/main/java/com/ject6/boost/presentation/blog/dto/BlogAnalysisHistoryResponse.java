package com.ject6.boost.presentation.blog.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record BlogAnalysisHistoryResponse(
        List<HistoryItem> content,
        int totalElements,
        int visibleCount
) {
    public record HistoryItem(
            Long id,
            String channelUrl,
            OffsetDateTime analyzedAt,
            boolean isLocked
    ) {}
}
