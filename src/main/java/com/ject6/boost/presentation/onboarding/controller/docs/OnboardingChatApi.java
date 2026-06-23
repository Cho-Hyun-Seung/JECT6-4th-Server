package com.ject6.boost.presentation.onboarding.controller.docs;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.onboarding.dto.OnboardingRecommendResponse;
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepRequest;
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "온보딩", description = "온보딩 응답 및 추천 API")
public interface OnboardingChatApi {

    @Operation(
            summary = "온보딩 단계 응답 저장",
            description = "step: 1(카테고리), 2(블로그 운영 여부), 3(공고 유형), 4(활동 수준), 5(선호 지역), 6(활동 플랫폼). "
                    + "step=1 answer 허용값: FOOD, BEAUTY, FASHION, TRAVEL, TECH_IT, LIVING, PET, CULTURE, ETC. "
                    + "step=3 answer 허용값: VISIT, DELIVERY, REPORTER, REVIEW, PAYBACK. "
                    + "step=6 activityTypes 허용값: BLOG, INSTAGRAM, YOUTUBE, TIKTOK, ETC"
    )
    ApiResponse<OnboardingStepResponse> saveStep(OnboardingStepRequest request);

    @Operation(summary = "온보딩 추천 공고 조회", description = "6단계가 완료된 sessionId로 추천 공고를 조회합니다.")
    ApiResponse<OnboardingRecommendResponse> getRecommendations(String sessionId);
}
