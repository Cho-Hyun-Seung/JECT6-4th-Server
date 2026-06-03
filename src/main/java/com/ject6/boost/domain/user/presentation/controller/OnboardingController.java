package com.ject6.boost.domain.user.presentation.controller;

import com.ject6.boost.common.dto.ApiResponse;
import com.ject6.boost.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.domain.user.application.service.OnboardingService;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileRequest;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController implements OnboardingApi {

    private final OnboardingService onboardingService;

    @PostMapping("/profile")
    public ApiResponse<OnboardingProfileResponse> updateProfile(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody OnboardingProfileRequest request
    ) {
        return ApiResponse.success(onboardingService.updateProfile(principal, request));
    }
}
