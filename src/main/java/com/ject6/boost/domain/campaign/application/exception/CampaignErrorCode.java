package com.ject6.boost.domain.campaign.application.exception;

import com.ject6.boost.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CampaignErrorCode implements ErrorCode {

    CAMPAIGN_NOT_FOUND("C001", "공고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

}
