package com.ject6.boost.presentation.user.controller;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.application.auth.service.AuthService;
import com.ject6.boost.presentation.auth.handler.OAuth2LoginSuccessHandler;
import com.ject6.boost.application.user.service.UserService;
import com.ject6.boost.presentation.user.controller.docs.UserApi;
import com.ject6.boost.presentation.user.dto.BlogLinkRequest;
import com.ject6.boost.presentation.user.dto.BlogLinkResponse;
import com.ject6.boost.presentation.user.dto.NicknameCheckResponse;
import com.ject6.boost.presentation.user.dto.ProfileRequest;
import com.ject6.boost.presentation.user.dto.ProfileResponse;
import com.ject6.boost.presentation.user.dto.RandomNicknameResponse;
import com.ject6.boost.presentation.user.dto.UserMeResponse;
import com.ject6.boost.presentation.user.dto.UserProfileUpdateRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Profile("!mock")
public class UserController implements UserApi {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> getMe(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ApiResponse.success(userService.getMe(principal));
    }

    @PatchMapping("/me")
    @Override
    public ApiResponse<UserMeResponse> updateProfile(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody UserProfileUpdateRequest request
    ) {
        return ApiResponse.success(userService.updateProfile(principal, request));
    }

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProfileResponse> createProfile(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody ProfileRequest request
    ) {
        return ApiResponse.success(userService.createProfile(principal, request));
    }

    @PostMapping("/me/blog")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BlogLinkResponse> linkBlog(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody BlogLinkRequest request
    ) {
        return ApiResponse.success(userService.linkBlog(principal, request));
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
