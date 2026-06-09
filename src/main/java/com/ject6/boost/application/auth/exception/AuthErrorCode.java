package com.ject6.boost.application.auth.exception;

import com.ject6.boost.application.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthErrorCode implements ErrorCode {
    OAUTH_AUTHORIZATION_CODE_REQUIRED("AUTH-001", "OAuth 인증 코드가 필요합니다.", HttpStatus.BAD_REQUEST),
    OAUTH_PROVIDER_REQUIRED("AUTH-002", "OAuth 제공자 정보가 필요합니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_OAUTH_PROVIDER("AUTH-003", "지원하지 않는 OAuth 제공자입니다.", HttpStatus.BAD_REQUEST),
    OAUTH_ACCESS_TOKEN_REQUEST_FAILED("AUTH-004", "OAuth 액세스 토큰 요청에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    OAUTH_USER_INFO_REQUEST_FAILED("AUTH-005", "OAuth 사용자 정보 요청에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    OAUTH_USER_ID_MISSING("AUTH-006", "OAuth 사용자 식별자를 확인할 수 없습니다.", HttpStatus.UNAUTHORIZED),
    OAUTH_PROVIDER_CONFIGURATION_REQUIRED("AUTH-007", "OAuth 제공자 설정이 필요합니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    LOGIN_TOKEN_ISSUE_FAILED("AUTH-008", "로그인 토큰 발급에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REFRESH_TOKEN_REQUIRED("AUTH-009", "리프레시 토큰이 필요합니다.", HttpStatus.BAD_REQUEST),
    REFRESH_SESSION_NOT_FOUND("AUTH-010", "리프레시 세션이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    OAUTH_REDIRECT_URI_REQUIRED("AUTH-011", "OAuth redirect_uri가 필요합니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
