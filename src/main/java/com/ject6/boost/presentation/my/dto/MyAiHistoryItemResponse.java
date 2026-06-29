package com.ject6.boost.presentation.my.dto;

import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import java.time.format.DateTimeFormatter;

public record MyAiHistoryItemResponse(
        Long historyId,
        String diagnosisDate,
        Long documentId
) {
    public static MyAiHistoryItemResponse from(BlogAnalysisResult result) {
        String diagnosisDate = result.getCreatedAt() == null
                ? null
                : result.getCreatedAt().toLocalDate().format(DateTimeFormatter.BASIC_ISO_DATE);
        Long documentId = null;
        if (result.getResult() != null) {
            Object raw = result.getResult().get("documentId");
            if (raw instanceof Number n) documentId = n.longValue();
        }
        return new MyAiHistoryItemResponse(result.getId(), diagnosisDate, documentId);
    }
}
