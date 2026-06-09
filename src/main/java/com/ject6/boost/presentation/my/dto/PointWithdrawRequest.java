package com.ject6.boost.presentation.my.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PointWithdrawRequest(
        @NotNull @Min(5000) Integer amount,
        @NotBlank String bankName,
        @NotBlank String accountNumber,
        @NotBlank String accountHolder
) {}
