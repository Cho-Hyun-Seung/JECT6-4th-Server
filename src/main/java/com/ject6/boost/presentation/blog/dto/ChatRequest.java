package com.ject6.boost.presentation.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatRequest(
        @NotBlank(message = "세션 ID가 필요합니다.") String sessionId,
        @NotNull(message = "분석 문서 ID가 필요합니다.") Long documentId,
        @NotBlank(message = "메시지를 입력해 주세요.") String message
) {}
