package com.ject6.boost.infrastructure.blog.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

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
            @JsonProperty("summary")         String summary,
            @JsonProperty("key_topics")      List<String> keyTopics,
            @JsonProperty("tone")            String tone,
            @JsonProperty("target_audience") String targetAudience,
            @JsonProperty("suggestions")     List<String> suggestions
    ) {}
}
