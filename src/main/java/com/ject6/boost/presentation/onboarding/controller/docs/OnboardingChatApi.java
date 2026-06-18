package com.ject6.boost.presentation.onboarding.controller.docs;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.onboarding.dto.OnboardingRecommendResponse;
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepRequest;
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Onboarding Chat", description = "Onboarding chat API")
public interface OnboardingChatApi {

    @Operation(
            summary = "Save onboarding step response",
            description = "step: 1(category), 2(blog operation), 3(campaign type), 4(activity level), 5(preferred regions), 6(activity platforms)"
    )
    ApiResponse<OnboardingStepResponse> saveStep(OnboardingStepRequest request);

    @Operation(summary = "Get onboarding recommendations", description = "Get recommendations by sessionId after all 6 steps are complete.")
    ApiResponse<OnboardingRecommendResponse> getRecommendations(String sessionId);
}
