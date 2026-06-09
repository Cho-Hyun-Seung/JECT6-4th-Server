package com.ject6.boost.application.user.exception;

import com.ject6.boost.application.common.exception.ErrorCode;
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
    INVALID_ACTIVITY_TYPE("USER-012", "활동 유형은 BLOG, INSTAGRAM, YOUTUBE, TIKTOK, ETC 중 하나여야 합니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    BLOG_URL_REQUIRED("USER-013", "블로그 URL이 필요합니다.", HttpStatus.BAD_REQUEST),
    INVALID_BLOG_URL("USER-014", "유효한 블로그 URL이 아닙니다.", HttpStatus.BAD_REQUEST),
    BLOG_PLATFORM_REQUIRED("USER-015", "블로그 플랫폼이 필요합니다.", HttpStatus.BAD_REQUEST),
    INVALID_BLOG_PLATFORM("USER-016", "블로그 플랫폼은 NAVER 중 하나여야 합니다.", HttpStatus.BAD_REQUEST),
    BLOG_POST_COUNT_INSUFFICIENT("BLOG_POST_COUNT_INSUFFICIENT", "블로그 포스팅이 5개 이상이어야 합니다.", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
