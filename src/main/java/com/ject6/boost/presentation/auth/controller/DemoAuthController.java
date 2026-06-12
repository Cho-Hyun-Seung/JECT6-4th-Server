package com.ject6.boost.presentation.auth.controller;

import com.ject6.boost.application.auth.service.AuthService;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.repository.UserRepository;
import com.ject6.boost.presentation.auth.dto.OAuthLoginResponse;
import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.infrastructure.common.security.jwt.JwtToken;
import com.ject6.boost.infrastructure.common.security.jwt.JwtTokenProvider;
import com.ject6.boost.infrastructure.auth.OAuthRedisKeys;
import com.ject6.boost.infrastructure.common.security.jwt.JwtProperties;
import com.ject6.boost.presentation.auth.dto.OAuthLoginUserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Profile("demo")
public class DemoAuthController {

    private static final long DEMO_USER_ID = 1L;
    private static final String TOKEN_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @PostMapping("/demo-login")
    public ApiResponse<OAuthLoginResponse> demoLogin() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(DEMO_USER_ID, List.of("USER"));

        JwtToken accessToken = jwtTokenProvider.issue(authenticatedUser);
        JwtToken refreshToken = jwtTokenProvider.issueRefreshToken(authenticatedUser);

        try {
            redisTemplate.opsForValue().set(
                    OAuthRedisKeys.REFRESH_KEY_PREFIX + refreshToken.id(),
                    objectMapper.writeValueAsString(authenticatedUser),
                    jwtProperties.getRefreshTokenTtl()
            );
        } catch (JsonProcessingException e) {
            log.warn("demo-login: refresh session save failed, continuing", e);
        }

        User demoUser = userRepository.findActiveById(DEMO_USER_ID).orElse(null);
        OAuthLoginUserResponse userResponse = demoUser != null
                ? OAuthLoginUserResponse.from(demoUser)
                : new OAuthLoginUserResponse(DEMO_USER_ID, "데모블로거", null,
                        com.ject6.boost.domain.user.constant.SubscriptionType.PREMIUM, 99, true);

        OAuthLoginResponse response = new OAuthLoginResponse(
                accessToken.token(),
                refreshToken.token(),
                accessToken.expiresIn(),
                TOKEN_TYPE,
                userResponse
        );

        log.info("demo-login issued for userId={}", DEMO_USER_ID);
        return ApiResponse.success(response);
    }
}
