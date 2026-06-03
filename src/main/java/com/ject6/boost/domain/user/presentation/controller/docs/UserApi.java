package com.ject6.boost.domain.user.presentation.controller.docs;

import com.ject6.boost.common.dto.ApiResponse;
import com.ject6.boost.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.domain.user.presentation.dto.ActivityChannelRequest;
import com.ject6.boost.domain.user.presentation.dto.ActivityChannelResponse;
import com.ject6.boost.domain.user.presentation.dto.NicknameCheckResponse;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileRequest;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileResponse;
import com.ject6.boost.domain.user.presentation.dto.RandomNicknameResponse;
import com.ject6.boost.domain.user.presentation.dto.UserMeResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

public interface UserApi {

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 프로필, 관심 카테고리, 활동 유형, 지역, 활동 채널 정보를 조회합니다.")
    ApiResponse<UserMeResponse> getMe(AuthenticatedUser principal);

    @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 닉네임, 관심 카테고리, 활동 유형, 지역 정보를 수정합니다.")
    ApiResponse<OnboardingProfileResponse> updateProfile(
            AuthenticatedUser principal,
            OnboardingProfileRequest request
    );

    @Operation(summary = "활동 채널 연동", description = "현재 로그인한 사용자의 활동 채널 유형과 URL을 저장합니다. 활동 채널 유형은 BLOG, INSTAGRAM, YOUTUBE, TIKTOK, ETC 중 하나여야 합니다.")
    ApiResponse<ActivityChannelResponse> linkActivityChannel(
            AuthenticatedUser principal,
            ActivityChannelRequest request
    );

    @Operation(summary = "활동 채널 연동 해제", description = "현재 로그인한 사용자의 특정 활동 채널 연동을 해제합니다.")
    ApiResponse<Void> unlinkActivityChannel(
            AuthenticatedUser principal,
            String activityType
    );

    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임을 사용할 수 있는지 확인합니다.")
    ApiResponse<NicknameCheckResponse> checkNickname(String nickname);

    @Operation(summary = "랜덤 닉네임 생성", description = "온보딩 또는 프로필 수정에서 사용할 랜덤 닉네임 후보를 생성합니다.")
    ApiResponse<RandomNicknameResponse> generateRandomNickname();

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 탈퇴 처리하고 access token session과 refresh token session을 제거합니다.")
    ApiResponse<Void> withdraw(
            AuthenticatedUser principal,
            String authorization,
            String refreshToken,
            HttpServletResponse response
    );
}