package com.ject6.boost.presentation.blog.dto;

import com.ject6.boost.infrastructure.blog.client.dto.AnalysisResultResponse;
import java.time.OffsetDateTime;
import java.util.List;

public record BlogAnalysisDetailResponse(
        Long documentId,
        String status,
        AnalysisData analysis,
        OffsetDateTime analyzedAt
) {
    public record AnalysisData(
            String summary,
            List<String> keyTopics,
            String tone,
            String targetAudience,
            List<String> suggestions
    ) {}

    public static BlogAnalysisDetailResponse from(AnalysisResultResponse r) {
        AnalysisData data = null;
        if (r.result() != null) {
            data = new AnalysisData(
                    r.result().summary(), r.result().keyTopics(),
                    r.result().tone(), r.result().targetAudience(), r.result().suggestions()
            );
        }
        return new BlogAnalysisDetailResponse(r.documentId(), r.status(), data, r.updatedAt());
    }
}
