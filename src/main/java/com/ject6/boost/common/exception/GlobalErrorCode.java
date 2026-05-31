package com.ject6.boost.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum GlobalErrorCode implements ErrorCode {
    VALIDATION_ERROR("COM-001", "입력값을 확인해 주세요.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("COM-002", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED_REQUEST("COM-003", "인증이 필요한 요청입니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("COM-004", "로그인이 만료되었습니다. 다시 로그인해 주세요.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("COM-005", "자동 로그인이 만료되었습니다. 다시 로그인해 주세요.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("COM-006", "유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
