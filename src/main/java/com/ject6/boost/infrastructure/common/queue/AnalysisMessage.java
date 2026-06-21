package com.ject6.boost.infrastructure.common.queue;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnalysisMessage(
        @JsonProperty("user_id")        Long userId,
        @JsonProperty("document_id")    Long documentId,
        @JsonProperty("correlation_id") String correlationId,
        @JsonProperty("analysis_mode")  String analysisMode
) {}
