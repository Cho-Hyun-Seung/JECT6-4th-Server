package com.ject6.boost.presentation.blog.dto;

import com.ject6.boost.infrastructure.blog.client.dto.AnalysisResultResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

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
            List<String> suggestions,
            Integer overallScore,
            Integer percentile,
            String blogType,
            String strengthSummary,
            String weaknessSummary,
            List<Map<String, Object>> topCategories,
            List<Map<String, Object>> metrics
    ) {}

    public static BlogAnalysisDetailResponse from(AnalysisResultResponse r) {
        AnalysisData data = null;
        if (r.result() != null) {
            AnalysisResultResponse.AnalysisResult res = r.result();
            data = new AnalysisData(
                    res.summary(), res.keyTopics(),
                    res.tone(), res.targetAudience(), res.suggestions(),
                    res.overallScore(), res.percentile(), res.blogType(),
                    res.strengthSummary(), res.weaknessSummary(),
                    res.topCategories(), res.metrics()
            );
        }
        return new BlogAnalysisDetailResponse(r.documentId(), r.status(), data, r.updatedAt());
    }
}
