package com.ject6.boost.application.onboarding.exception;

import com.ject6.boost.application.common.exception.ErrorCode;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OnboardingErrorCode implements ErrorCode {
    SESSION_NOT_FOUND("ONBOARD-001", "온보딩 세션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ONBOARDING_NOT_COMPLETE("ONBOARD-002", "온보딩이 완료되지 않았습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STEP("ONBOARD-003", "유효하지 않은 단계입니다. (1~6)", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
