package com.ject6.boost.application.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.infrastructure.common.security.jwt.JwtProperties;
import com.ject6.boost.infrastructure.common.security.jwt.JwtToken;
import com.ject6.boost.infrastructure.common.security.jwt.JwtTokenProvider;
import com.ject6.boost.presentation.auth.dto.OAuthLoginResult;
import com.ject6.boost.presentation.auth.dto.OAuthLoginResponse;
import com.ject6.boost.presentation.auth.dto.OAuthLoginUserResponse;
import com.ject6.boost.presentation.auth.dto.OAuthUserProfile;
import com.ject6.boost.presentation.auth.dto.TokenRefreshResponse;
import com.ject6.boost.domain.auth.OAuthProvider;
import com.ject6.boost.infrastructure.auth.OAuthRedisKeys;
import com.ject6.boost.application.auth.exception.AuthErrorCode;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.entity.UserOAuthAccount;
import com.ject6.boost.domain.user.repository.UserOAuthAccountRepository;
import com.ject6.boost.domain.user.repository.UserRepository;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final JwtProperties jwtProperties;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserOAuthAccountRepository userOAuthAccountRepository;

    /**
     * OAuth provider 정보로 로그인하고 서비스 토큰을 발급하는 함수.
     */
    @Transactional
    public OAuthLoginResult login(OAuthProvider provider, String providerUserId) {
        if (provider == null || !StringUtils.hasText(providerUserId)) {
            throw new BusinessException(AuthErrorCode.OAUTH_USER_ID_MISSING);
        }

        return login(new OAuthUserProfile(provider, providerUserId, null, null, null));
    }

    @Transactional
    public OAuthLoginResult login(OAuthUserProfile profile) {
        if (profile == null || profile.provider() == null || !StringUtils.hasText(profile.providerUserId())) {
            throw new BusinessException(AuthErrorCode.OAUTH_USER_ID_MISSING);
        }

        User user = findOrCreateUser(profile);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getId(), List.of("USER"));
        JwtToken accessToken = issueAccessToken(authenticatedUser);
        JwtToken refreshToken = issueRefreshToken(authenticatedUser);

        OAuthLoginResponse response = new OAuthLoginResponse(
                accessToken.token(),
                refreshToken.token(),
                accessToken.expiresIn(),
                TOKEN_TYPE,
                OAuthLoginUserResponse.from(user)
        );

        return new OAuthLoginResult(response, refreshToken.token(), refreshToken.expiresIn());
    }

    /**
     * refresh token을 검증하고 새로운 서비스 access token을 발급하는 함수.
     */
    public TokenRefreshResponse refresh(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_REQUIRED);
        }

        String refreshTokenId = jwtTokenProvider.validateRefreshTokenAndGetTokenId(refreshToken);
        String refreshSession = redisTemplate.opsForValue().get(OAuthRedisKeys.REFRESH_KEY_PREFIX + refreshTokenId);

        if (!StringUtils.hasText(refreshSession)) {
            throw new BusinessException(AuthErrorCode.REFRESH_SESSION_NOT_FOUND);
        }

        AuthenticatedUser authenticatedUser = readAuthenticatedUser(refreshSession);
        JwtToken accessToken = issueAccessToken(authenticatedUser);

        return new TokenRefreshResponse(
                accessToken.token(),
                TOKEN_TYPE,
                accessToken.expiresIn()
        );
    }

    /**
     * refresh token에 연결된 Redis 세션을 제거하는 함수.
     */
    public void logout(String accessToken, String refreshToken) {
        if (StringUtils.hasText(refreshToken)) {
            String refreshTokenId = jwtTokenProvider.validateRefreshTokenAndGetTokenId(refreshToken);
            redisTemplate.delete(OAuthRedisKeys.REFRESH_KEY_PREFIX + refreshTokenId);
        }
    }

    /**
     * 활성 OAuth 계정 사용자를 조회하거나 새 사용자 계정 연결을 생성하는 함수.
     */
    private User findOrCreateUser(OAuthUserProfile profile) {
        Optional<UserOAuthAccount> account =
                userOAuthAccountRepository.findActiveByProviderAndProviderUserId(
                        profile.provider(),
                        profile.providerUserId()
                );

        if (account.isPresent()) {
            User user = account.get().getUser();
            user.updateOAuthProfile(profile.nickname(), profile.profileImageUrl());
            return user;
        }

        User user = userRepository.save(User.create());
        user.updateOAuthProfile(profile.nickname(), profile.profileImageUrl());
        userOAuthAccountRepository.save(UserOAuthAccount.create(user, profile.provider(), profile.providerUserId()));
        return user;
    }

    /**
     * access token을 발급하는 함수.
     */
    private JwtToken issueAccessToken(AuthenticatedUser authenticatedUser) {
        return jwtTokenProvider.issue(authenticatedUser);
    }

    /**
     * refresh token을 발급하고 refresh 세션을 Redis에 저장하는 함수.
     */
    private JwtToken issueRefreshToken(AuthenticatedUser authenticatedUser) {
        JwtToken refreshToken = jwtTokenProvider.issueRefreshToken(authenticatedUser);
        String redisKey = OAuthRedisKeys.REFRESH_KEY_PREFIX + refreshToken.id();

        saveSession(redisKey, authenticatedUser, jwtProperties.getRefreshTokenTtl());
        return refreshToken;
    }

    /**
     * 인증 사용자 정보를 Redis JSON 세션으로 저장하는 함수.
     */
    private void saveSession(String redisKey, AuthenticatedUser authenticatedUser, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(
                    redisKey,
                    objectMapper.writeValueAsString(authenticatedUser),
                    ttl
            );
        } catch (JsonProcessingException exception) {
            throw new BusinessException(AuthErrorCode.LOGIN_TOKEN_ISSUE_FAILED);
        }
    }

    /**
     * Redis JSON 세션에서 인증 사용자 정보를 복원하는 함수.
     */
    private AuthenticatedUser readAuthenticatedUser(String session) {
        try {
            return objectMapper.readValue(session, AuthenticatedUser.class);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(AuthErrorCode.REFRESH_SESSION_NOT_FOUND);
        }
    }
}
