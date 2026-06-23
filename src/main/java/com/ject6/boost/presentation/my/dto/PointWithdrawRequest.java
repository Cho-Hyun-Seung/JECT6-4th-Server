package com.ject6.boost.presentation.my.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PointWithdrawRequest(
        @Schema(description = "출금 신청 금액. 최소 5,000P", example = "5000")
        @NotNull(message = "출금 금액이 필요합니다.")
        @Min(value = 5000, message = "최소 출금 금액은 5,000P입니다.")
        Integer amount,
        @Schema(description = "은행명")
        @NotBlank(message = "은행명이 필요합니다.") String bankName,
        @Schema(description = "계좌번호")
        @NotBlank(message = "계좌번호가 필요합니다.") String accountNumber,
        @Schema(description = "예금주명")
        @NotBlank(message = "예금주명이 필요합니다.") String accountHolder
) {}
