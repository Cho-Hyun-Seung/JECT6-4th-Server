package com.ject6.boost.domain.user.application.controller;

import com.ject6.boost.common.dto.ApiResponse;
import com.ject6.boost.common.security.AuthenticatedUser;
import com.ject6.boost.domain.user.application.dto.ActivityChannelRequest;
import com.ject6.boost.domain.user.application.dto.ActivityChannelResponse;
import com.ject6.boost.domain.user.application.dto.NicknameCheckResponse;
import com.ject6.boost.domain.user.application.dto.OnboardingProfileRequest;
import com.ject6.boost.domain.user.application.dto.OnboardingProfileResponse;
import com.ject6.boost.domain.user.application.dto.RandomNicknameResponse;
import com.ject6.boost.domain.user.application.dto.UserMeResponse;
import com.ject6.boost.domain.user.presentation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 프로필, 관심 카테고리, 활동 유형, 지역, 활동 채널 정보를 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<UserMeResponse> getMe(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success(userService.getMe(principal));
    }

    @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 닉네임, 관심 카테고리, 활동 유형, 지역 정보를 수정합니다.")
    @PatchMapping("/me")
    public ApiResponse<OnboardingProfileResponse> updateProfile(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody OnboardingProfileRequest request
    ) {
        return ApiResponse.success(userService.updateProfile(principal, request));
    }

    @Operation(summary = "활동 채널 연동", description = "현재 로그인한 사용자의 활동 채널 유형과 주소를 저장합니다. 활동 채널 유형은 BLOG, INSTAGRAM, YOUTUBE, TIKTOK, ETC 중 하나여야 합니다.")
    @PostMapping("/me/activity-channel")
    public ApiResponse<ActivityChannelResponse> linkActivityChannel(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody ActivityChannelRequest request
    ) {
        return ApiResponse.success(userService.linkActivityChannel(principal, request));
    }

    @Operation(summary = "활동 채널 연동 해제", description = "현재 로그인한 사용자의 특정 활동 채널 연동을 해제합니다.")
    @DeleteMapping("/me/activity-channel")
    public ApiResponse<Void> unlinkActivityChannel(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam String activityType
    ) {
        userService.unlinkActivityChannel(principal, activityType);
        return ApiResponse.success(null);
    }

    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임을 사용할 수 있는지 확인합니다.")
    @GetMapping("/nickname/check")
    public ApiResponse<NicknameCheckResponse> checkNickname(@RequestParam String nickname) {
        return ApiResponse.success(userService.checkNickname(nickname));
    }

    @Operation(summary = "랜덤 닉네임 생성", description = "온보딩 또는 프로필 수정에서 사용할 랜덤 닉네임 후보를 생성합니다.")
    @GetMapping("/nickname/random")
    public ApiResponse<RandomNicknameResponse> generateRandomNickname() {
        return ApiResponse.success(userService.generateRandomNickname());
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 탈퇴 처리합니다.")
    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(@AuthenticationPrincipal AuthenticatedUser principal) {
        userService.withdraw(principal);
        return ApiResponse.success(null);
    }
}
