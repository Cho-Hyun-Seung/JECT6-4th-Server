package com.ject6.boost.presentation.onboarding.dto;

public record OnboardingStepResponse(
        String sessionId,
        int step,
        boolean isComplete,
        Integer nextStep
) {}
