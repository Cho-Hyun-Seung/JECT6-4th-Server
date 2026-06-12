package com.ject6.boost.infrastructure.blog.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record AnalysisResultResponse(
        @JsonProperty("id")             Long id,
        @JsonProperty("document_id")    Long documentId,
        @JsonProperty("status")         String status,
        @JsonProperty("result")         AnalysisResult result,
        @JsonProperty("error_message")  String errorMessage,
        @JsonProperty("created_at")     OffsetDateTime createdAt,
        @JsonProperty("updated_at")     OffsetDateTime updatedAt
) {
    public record AnalysisResult(
            @JsonProperty("summary")           String summary,
            @JsonProperty("key_topics")        List<String> keyTopics,
            @JsonProperty("tone")              String tone,
            @JsonProperty("target_audience")   String targetAudience,
            @JsonProperty("suggestions")       List<String> suggestions,
            @JsonProperty("overall_score")     Integer overallScore,
            @JsonProperty("percentile")        Integer percentile,
            @JsonProperty("blog_type")         String blogType,
            @JsonProperty("strength_summary")  String strengthSummary,
            @JsonProperty("weakness_summary")  String weaknessSummary,
            @JsonProperty("top_categories")    List<Map<String, Object>> topCategories,
            @JsonProperty("metrics")           List<Map<String, Object>> metrics
    ) {}
}
