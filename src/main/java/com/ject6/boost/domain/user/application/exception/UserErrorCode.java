package com.ject6.boost.domain.user.application.exception;

import com.ject6.boost.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserErrorCode implements ErrorCode {
    AUTHENTICATED_USER_REQUIRED("USER-001", "인증된 사용자 정보가 필요합니다.", HttpStatus.UNAUTHORIZED),
    NICKNAME_REQUIRED("USER-002", "닉네임이 필요합니다.", HttpStatus.BAD_REQUEST),
    INVALID_NICKNAME_LENGTH("USER-003", "닉네임은 2자 이상 100자 이하로 입력해 주세요.", HttpStatus.BAD_REQUEST),
    CATEGORY_REQUIRED("USER-004", "관심 카테고리를 하나 이상 선택해 주세요.", HttpStatus.BAD_REQUEST),
    ACTIVITY_TYPE_REQUIRED("USER-005", "활동 유형을 하나 이상 선택해 주세요.", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY_TYPE("USER-006", "관심 카테고리는 FOOD, BEAUTY, CULTURE, TRAVEL, TECH_IT, PET, LIVING, FASHION, ETC 중 하나여야 합니다.", HttpStatus.BAD_REQUEST),
    REGION_NOT_FOUND("USER-007", "존재하지 않는 지역입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER-008", "사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    ACTIVITY_CHANNEL_TYPE_REQUIRED("USER-009", "활동 채널 유형이 필요합니다.", HttpStatus.BAD_REQUEST),
    ACTIVITY_CHANNEL_URL_REQUIRED("USER-010", "활동 채널 URL이 필요합니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACTIVITY_CHANNEL_URL("USER-011", "활동 채널 URL은 https://로 시작해야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACTIVITY_TYPE("USER-012", "활동 유형은 BLOG, INSTAGRAM, YOUTUBE, TIKTOK, ETC 중 하나여야 합니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}