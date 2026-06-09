package com.ject6.boost.presentation.auth.controller.docs;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.auth.dto.OAuthLoginRequest;
import com.ject6.boost.presentation.auth.dto.OAuthLoginResponse;
import com.ject6.boost.presentation.auth.dto.TokenRefreshResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.RedirectView;

public interface AuthApi {

    @Operation(summary = "소셜 로그인 리다이렉트", description = "제공자에 해당하는 소셜 로그인 페이지로 이동합니다.")
    RedirectView login(String provider);

    @Operation(summary = "소셜 OAuth2 로그인", description = "OAuth authorization code를 서비스 토큰으로 교환합니다.")
    ApiResponse<OAuthLoginResponse> login(String provider, OAuthLoginRequest request);

    @Operation(summary = "로그아웃", description = "현재 refresh token session을 제거하고 refresh token 쿠키를 만료합니다.")
    ApiResponse<Void> logout(String authorization, String refreshToken, HttpServletResponse response);

    @Operation(summary = "access token 재발급", description = "쿠키에 저장된 refresh token으로 새 access token을 발급합니다.")
    ApiResponse<TokenRefreshResponse> refresh(String refreshToken);
}
