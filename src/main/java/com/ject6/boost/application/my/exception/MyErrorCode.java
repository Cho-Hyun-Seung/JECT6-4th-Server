package com.ject6.boost.application.my.exception;

import com.ject6.boost.application.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MyErrorCode implements ErrorCode {
    USER_CAMPAIGN_NOT_FOUND("MY-001", "체험단 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_BALANCE("MY-002", "포인트 잔액이 부족합니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    BELOW_MINIMUM_WITHDRAW("MY-003", "최소 출금 금액(5,000P)을 충족하지 않습니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    POINT_WALLET_NOT_FOUND("MY-004", "포인트 지갑을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
