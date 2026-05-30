package com.ject6.boost.domain.auth.infrastructure.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ject6.boost.domain.auth.application.dto.OAuthLoginRequest;
import com.ject6.boost.domain.auth.application.dto.OAuthUserProfile;
import com.ject6.boost.domain.auth.domain.OAuthProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KakaoOAuthClient extends AbstractAuthorizationCodeOAuthClient {

    public KakaoOAuthClient(OAuthClientProperties properties, RestClient.Builder restClientBuilder) {
        super(properties, restClientBuilder.build());
    }

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public OAuthUserProfile fetchUserProfile(OAuthLoginRequest request) {
        OAuthTokenResponse token = requestToken(request);
        KakaoUserInfoResponse userInfo = requestUserInfo(token.accessToken(), KakaoUserInfoResponse.class);
        return userInfo.toUserProfile();
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record KakaoUserInfoResponse(
            Long id,
            Properties properties,
            KakaoAccount kakaoAccount
    ) {

        OAuthUserProfile toUserProfile() {
            if (id == null) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "KAKAO user id is missing."
                );
            }

            String nickname = properties == null ? null : properties.nickname;
            String profileImageUrl = properties == null ? null : properties.profileImage;
            String email = kakaoAccount == null ? null : kakaoAccount.email;

            return new OAuthUserProfile(OAuthProvider.KAKAO, String.valueOf(id), nickname, email, profileImageUrl);
        }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record Properties(
            String nickname,
            String profileImage
    ) {
    }

    private record KakaoAccount(String email) {
    }
}
