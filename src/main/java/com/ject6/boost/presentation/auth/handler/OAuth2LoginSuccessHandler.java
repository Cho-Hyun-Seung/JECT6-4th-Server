package com.ject6.boost.presentation.auth.handler;

import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.presentation.common.security.handler.SecurityErrorResponseWriter;
import com.ject6.boost.application.auth.exception.AuthErrorCode;
import com.ject6.boost.application.auth.service.AuthService;
import com.ject6.boost.domain.auth.OAuthProvider;
import com.ject6.boost.presentation.auth.dto.OAuthLoginResult;
import com.ject6.boost.presentation.auth.dto.OAuthLoginResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final AuthService authService;
    private final SecurityErrorResponseWriter securityErrorResponseWriter;

    @Value("${app.frontend.oauth-callback-url:http://localhost:3000/auth/callback}")
    private String frontendOAuthCallbackUrl;

    /**
     * Handles OAuth2 login success and redirects to the frontend callback with service tokens.
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        try {
            LoginIdentity loginIdentity = toLoginIdentity(authentication);
            OAuthLoginResult loginResult = authService.login(
                    loginIdentity.provider(),
                    loginIdentity.providerUserId()
            );
            OAuthLoginResponse loginResponse = loginResult.response();

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();

            response.addHeader(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(loginResult).toString());
            response.sendRedirect(createFrontendCallbackUrl(loginResponse));
        } catch (BusinessException exception) {
            securityErrorResponseWriter.write(response, exception.getErrorCode());
        }
    }

    /**
     * Creates a Set-Cookie value for delivering the refresh token as an HttpOnly cookie.
     */
    private ResponseCookie createRefreshTokenCookie(OAuthLoginResult loginResult) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, loginResult.refreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth/refresh")
                .maxAge(Duration.ofSeconds(loginResult.refreshTokenExpiresIn()))
                .build();
    }

    private String createFrontendCallbackUrl(OAuthLoginResponse loginResponse) {
        return UriComponentsBuilder.fromUriString(frontendOAuthCallbackUrl)
                .queryParam("accessToken", loginResponse.accessToken())
                .queryParam("refreshToken", loginResponse.refreshToken())
                .queryParam("tokenType", loginResponse.tokenType())
                .queryParam("expiresIn", loginResponse.expiresIn())
                .build()
                .encode()
                .toUriString();
    }

    /**
     * Extracts provider login identity from Spring Security OAuth2 authentication.
     */
    private LoginIdentity toLoginIdentity(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken oauth2Authentication)
                || !(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
            throw new BusinessException(AuthErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }

        OAuthProvider provider = OAuthProvider.fromRegistrationId(
                oauth2Authentication.getAuthorizedClientRegistrationId()
        );

        Object id = oauth2User.getAttribute("id");
        if (id == null) {
            throw new BusinessException(AuthErrorCode.OAUTH_USER_ID_MISSING);
        }

        return new LoginIdentity(
                provider,
                String.valueOf(id)
        );
    }

    private record LoginIdentity(
            OAuthProvider provider,
            String providerUserId
    ) {
    }
}
