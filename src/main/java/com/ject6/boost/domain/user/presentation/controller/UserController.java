package com.ject6.boost.domain.user.presentation.controller;

import com.ject6.boost.common.dto.ApiResponse;
import com.ject6.boost.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.domain.auth.presentation.handler.OAuth2LoginSuccessHandler;
import com.ject6.boost.domain.auth.application.service.AuthService;
import com.ject6.boost.domain.user.application.service.UserService;
import com.ject6.boost.domain.user.presentation.dto.ActivityChannelRequest;
import com.ject6.boost.domain.user.presentation.dto.ActivityChannelResponse;
import com.ject6.boost.domain.user.presentation.dto.NicknameCheckResponse;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileRequest;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileResponse;
import com.ject6.boost.domain.user.presentation.dto.RandomNicknameResponse;
import com.ject6.boost.domain.user.presentation.dto.UserMeResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> getMe(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success(userService.getMe(principal));
    }

    @PatchMapping("/me")
    public ApiResponse<OnboardingProfileResponse> updateProfile(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody OnboardingProfileRequest request
    ) {
        return ApiResponse.success(userService.updateProfile(principal, request));
    }

    @PostMapping("/me/activity-channel")
    public ApiResponse<ActivityChannelResponse> linkActivityChannel(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody ActivityChannelRequest request
    ) {
        return ApiResponse.success(userService.linkActivityChannel(principal, request));
    }

    @DeleteMapping("/me/activity-channel")
    public ApiResponse<Void> unlinkActivityChannel(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam String activityType
    ) {
        userService.unlinkActivityChannel(principal, activityType);
        return ApiResponse.success(null);
    }

    @GetMapping("/nickname/check")
    public ApiResponse<NicknameCheckResponse> checkNickname(@RequestParam String nickname) {
        return ApiResponse.success(userService.checkNickname(nickname));
    }

    @GetMapping("/nickname/random")
    public ApiResponse<RandomNicknameResponse> generateRandomNickname() {
        return ApiResponse.success(userService.generateRandomNickname());
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @CookieValue(name = OAuth2LoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        userService.withdraw(principal);
        authService.logout(extractBearerToken(authorization), refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, expireRefreshTokenCookie().toString());
        return ApiResponse.success(null);
    }

    private String extractBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorization.substring(BEARER_PREFIX.length());
    }

    private ResponseCookie expireRefreshTokenCookie() {
        return ResponseCookie.from(OAuth2LoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth/refresh")
                .maxAge(Duration.ZERO)
                .build();
    }
}
