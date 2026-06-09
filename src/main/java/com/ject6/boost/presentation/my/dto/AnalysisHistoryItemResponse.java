package com.ject6.boost.presentation.my.dto;

import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import java.time.OffsetDateTime;

public record AnalysisHistoryItemResponse(
        Long id,
        String channelUrl,
        OffsetDateTime analyzedAt,
        boolean isLocked
) {
    public static AnalysisHistoryItemResponse from(BlogAnalysisResult r, boolean locked) {
        return new AnalysisHistoryItemResponse(
                r.getId(),
                r.getBlog() != null ? r.getBlog().getBlogUrl() : null,
                r.getCreatedAt(),
                locked
        );
    }
}
