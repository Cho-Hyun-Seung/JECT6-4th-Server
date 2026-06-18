package com.ject6.boost.presentation.onboarding.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record OnboardingStepRequest(
        String sessionId,
        @NotNull @Min(1) @Max(6) Integer step,
        String answer,
        List<String> activityTypes,
        List<Long> regionIds
) {}
