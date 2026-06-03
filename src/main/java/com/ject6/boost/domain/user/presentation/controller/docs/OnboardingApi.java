package com.ject6.boost.domain.user.presentation.controller.docs;

import com.ject6.boost.common.dto.ApiResponse;
import com.ject6.boost.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileRequest;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileResponse;
import io.swagger.v3.oas.annotations.Operation;

public interface OnboardingApi {

    @Operation(summary = "온보딩 정보 입력", description = "현재 로그인한 사용자의 닉네임, 관심 카테고리, 활동 유형, 지역 정보를 저장하고 온보딩을 완료합니다.")
    ApiResponse<OnboardingProfileResponse> updateProfile(
            AuthenticatedUser principal,
            OnboardingProfileRequest request
    );
}