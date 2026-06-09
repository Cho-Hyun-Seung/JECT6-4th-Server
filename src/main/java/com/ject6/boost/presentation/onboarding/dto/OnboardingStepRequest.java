package com.ject6.boost.presentation.onboarding.dto;

import jakarta.validation.constraints.*;

public record OnboardingStepRequest(
        String sessionId,
        @NotNull @Min(1) @Max(4) Integer step,
        @NotBlank String answer
) {}
