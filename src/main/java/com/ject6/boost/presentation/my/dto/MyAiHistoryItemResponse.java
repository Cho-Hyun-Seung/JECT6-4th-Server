package com.ject6.boost.presentation.my.dto;

import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import java.time.format.DateTimeFormatter;

public record MyAiHistoryItemResponse(
        Long historyId,
        String diagnosisDate
) {
    public static MyAiHistoryItemResponse from(BlogAnalysisResult result) {
        String diagnosisDate = result.getCreatedAt() == null
                ? null
                : result.getCreatedAt().toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE);
        return new MyAiHistoryItemResponse(result.getId(), diagnosisDate);
    }
}
