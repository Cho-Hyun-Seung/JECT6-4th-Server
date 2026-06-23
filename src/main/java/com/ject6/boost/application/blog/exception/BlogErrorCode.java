package com.ject6.boost.application.blog.exception;

import com.ject6.boost.application.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BlogErrorCode implements ErrorCode {
    ANALYSIS_NOT_FOUND("BLOG-001", "분석 결과를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_AI_CREDIT("BLOG-002", "AI 크레딧이 부족합니다.", HttpStatus.PAYMENT_REQUIRED),
    BLOG_NOT_CONNECTED("BLOG-003", "블로그가 연동되어 있지 않습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    CHAT_TOKEN_LIMIT_EXCEEDED("BLOG-004", "채팅 토큰 한도를 초과했습니다. 새 세션으로 다시 시작해 주세요.", HttpStatus.BAD_REQUEST),
    CHAT_RATE_LIMIT_EXCEEDED("BLOG-005", "채팅 요청 한도를 초과했습니다. 잠시 후 다시 시도해 주세요.", HttpStatus.TOO_MANY_REQUESTS),
    ANALYZE_SERVER_ERROR("BLOG-006", "AI 분석 서버 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
    INVALID_ANALYSIS_MODE("BLOG-007", "analysisMode는 FULL_BLOG 또는 POST 중 하나여야 합니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
