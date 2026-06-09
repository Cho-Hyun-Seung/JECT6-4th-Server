package com.ject6.boost.presentation.onboarding.controller.docs;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.onboarding.dto.OnboardingRecommendResponse;
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepRequest;
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Onboarding Chat", description = "비로그인 온보딩 채팅 API")
public interface OnboardingChatApi {

    @Operation(summary = "온보딩 단계별 응답 저장", description = "step: 1(관심카테고리)|2(블로그운영여부)|3(협찬선호)|4(활동수준)")
    ApiResponse<OnboardingStepResponse> saveStep(OnboardingStepRequest request);

    @Operation(summary = "온보딩 기반 추천 공고 조회", description = "4단계 완료 후 session_id로 조회")
    ApiResponse<OnboardingRecommendResponse> getRecommendations(String sessionId);
}
