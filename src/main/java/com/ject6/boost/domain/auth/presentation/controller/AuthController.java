package com.ject6.boost.domain.auth.presentation.controller;

import com.ject6.boost.common.dto.ApiResponse;
import com.ject6.boost.domain.auth.application.service.AuthService;
import com.ject6.boost.domain.auth.domain.OAuthProvider;
import com.ject6.boost.domain.auth.infrastructure.OAuthAuthorizationCodeClient;
import com.ject6.boost.domain.auth.presentation.controller.docs.AuthApi;
import com.ject6.boost.domain.auth.presentation.dto.OAuthLoginRequest;
import com.ject6.boost.domain.auth.presentation.dto.OAuthLoginResponse;
import com.ject6.boost.domain.auth.presentation.dto.OAuthUserProfile;
import com.ject6.boost.domain.auth.presentation.dto.TokenRefreshResponse;
import com.ject6.boost.domain.auth.presentation.handler.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final OAuthAuthorizationCodeClient oAuthAuthorizationCodeClient;

    @GetMapping("/login/{provider}")
    public RedirectView login(@PathVariable String provider) {
        OAuthProvider oauthProvider = OAuthProvider.from(provider);
        return new RedirectView("/oauth2/authorization/" + oauthProvider.registrationId());
    }

    @PostMapping("/login/{provider}")
    public ApiResponse<OAuthLoginResponse> login(@PathVariable String provider, @RequestBody OAuthLoginRequest request) {
        OAuthProvider oauthProvider = OAuthProvider.from(provider);
        OAuthUserProfile profile = oAuthAuthorizationCodeClient.fetchProfile(oauthProvider, request);
        return ApiResponse.success(authService.login(profile).response());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @CookieValue(name = OAuth2LoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(extractBearerToken(authorization), refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, expireRefreshTokenCookie().toString());
        return ApiResponse.success(null);
    }

    @PostMapping("/refresh")
    @Override
    public ApiResponse<TokenRefreshResponse> refresh(
            @CookieValue(name = OAuth2LoginSuccessHandler.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken
    ) {
        return ApiResponse.success(authService.refresh(refreshToken));
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
