package com.ject6.boost.presentation.auth.controller.docs;

import com.ject6.boost.presentation.auth.dto.OAuthLoginRequest;
import com.ject6.boost.presentation.auth.dto.OAuthLoginResponse;
import com.ject6.boost.presentation.auth.dto.TokenRefreshResponse;
import com.ject6.boost.presentation.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.RedirectView;

public interface AuthApi {

    @Operation(summary = "소셜 로그인 리다이렉트", description = "제공자에 해당하는 소셜 로그인 페이지로 이동합니다.")
    RedirectView login(
            @Parameter(
                    description = "OAuth 제공자. 허용값: kakao, google, naver",
                    schema = @Schema(allowableValues = {"kakao", "google", "naver"})
            )
            String provider);

    @Operation(summary = "소셜 OAuth2 로그인", description = "OAuth authorization code를 서비스 토큰으로 교환합니다.")
    ApiResponse<OAuthLoginResponse> login(
            @Parameter(
                    description = "OAuth 제공자. 허용값: kakao, google, naver",
                    schema = @Schema(allowableValues = {"kakao", "google", "naver"})
            )
            String provider,
            OAuthLoginRequest request);

    @Operation(summary = "로그아웃", description = "현재 리프레시 토큰 세션을 제거하고 리프레시 토큰 쿠키를 만료합니다.")
    ApiResponse<Void> logout(String authorization, String refreshToken, HttpServletResponse response);

    @Operation(summary = "액세스 토큰 재발급", description = "쿠키에 저장된 리프레시 토큰으로 새 액세스 토큰을 발급합니다.")
    ApiResponse<TokenRefreshResponse> refresh(String refreshToken);
}
