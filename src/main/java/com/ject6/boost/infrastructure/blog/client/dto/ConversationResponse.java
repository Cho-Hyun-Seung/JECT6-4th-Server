package com.ject6.boost.infrastructure.blog.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConversationResponse(
        @JsonProperty("session_id")        String sessionId,
        @JsonProperty("reply")             String reply,
        @JsonProperty("tokens_used")       int tokensUsed,
        @JsonProperty("tokens_remaining")  int tokensRemaining
) {}
