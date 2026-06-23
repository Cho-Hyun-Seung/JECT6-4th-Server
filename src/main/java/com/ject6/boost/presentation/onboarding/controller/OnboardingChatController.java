package com.ject6.boost.presentation.onboarding.controller;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.application.onboarding.service.OnboardingChatService;
import com.ject6.boost.presentation.onboarding.controller.docs.OnboardingChatApi;
import com.ject6.boost.presentation.onboarding.dto.OnboardingRecommendResponse;
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepRequest;
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
@Profile("!mock")
public class OnboardingChatController implements OnboardingChatApi {

    private final OnboardingChatService onboardingChatService;

    @PostMapping("/response")
    @Override
    public ApiResponse<OnboardingStepResponse> saveStep(@Valid @RequestBody OnboardingStepRequest request) {
        return ApiResponse.success(onboardingChatService.saveStep(request));
    }

    @GetMapping("/recommendations")
    @Override
    public ApiResponse<OnboardingRecommendResponse> getRecommendations(@RequestParam String sessionId) {
        return ApiResponse.success(onboardingChatService.getRecommendations(sessionId));
    }
}
