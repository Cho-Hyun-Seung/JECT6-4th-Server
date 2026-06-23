package com.ject6.boost.presentation.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OnboardingStepRequest(
        @Schema(description = "온보딩 세션 ID. 첫 요청에서는 생략할 수 있습니다.")
        String sessionId,
        @Schema(description = "온보딩 단계. 허용값: 1, 2, 3, 4, 5, 6", allowableValues = {"1", "2", "3", "4", "5", "6"})
        @NotNull(message = "온보딩 단계가 필요합니다.")
        @Min(value = 1, message = "온보딩 단계는 1 이상이어야 합니다.")
        @Max(value = 6, message = "온보딩 단계는 6 이하여야 합니다.")
        Integer step,
        @Schema(
                description = "단계별 응답값. step=1 허용값: FOOD, BEAUTY, FASHION, TRAVEL, TECH_IT, LIVING, PET, CULTURE, ETC. step=3 허용값: VISIT, DELIVERY, REPORTER, REVIEW, PAYBACK"
        )
        String answer,
        @Schema(
                description = "활동 플랫폼 목록. step=6에서 사용합니다. 허용값: BLOG, INSTAGRAM, YOUTUBE, TIKTOK, ETC",
                allowableValues = {"BLOG", "INSTAGRAM", "YOUTUBE", "TIKTOK", "ETC"}
        )
        List<String> activityTypes,
        @Schema(description = "선호 지역 ID 목록. step=5에서 사용합니다.")
        List<Long> regionIds
) {}
