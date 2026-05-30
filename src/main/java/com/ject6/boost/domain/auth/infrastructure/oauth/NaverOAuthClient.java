package com.ject6.boost.domain.auth.infrastructure.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ject6.boost.domain.auth.application.dto.OAuthLoginRequest;
import com.ject6.boost.domain.auth.application.dto.OAuthUserProfile;
import com.ject6.boost.domain.auth.domain.OAuthProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class NaverOAuthClient extends AbstractAuthorizationCodeOAuthClient {

    public NaverOAuthClient(OAuthClientProperties properties, RestClient.Builder restClientBuilder) {
        super(properties, restClientBuilder.build());
    }

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public OAuthUserProfile fetchUserProfile(OAuthLoginRequest request) {
        OAuthTokenResponse token = requestToken(request);
        NaverUserInfoResponse userInfo = requestUserInfo(token.accessToken(), NaverUserInfoResponse.class);
        return userInfo.toUserProfile();
    }

    @Override
    protected void addProviderTokenParameters(LinkedMultiValueMap<String, String> form, OAuthLoginRequest request) {
        if (StringUtils.hasText(request.state())) {
            form.add("state", request.state());
        }
    }

    private record NaverUserInfoResponse(
            String resultcode,
            String message,
            Response response
    ) {

        OAuthUserProfile toUserProfile() {
            if (response == null || response.id == null) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "NAVER user id is missing."
                );
            }

            return new OAuthUserProfile(
                    OAuthProvider.NAVER,
                    response.id,
                    response.nickname,
                    response.email,
                    response.profileImage
            );
        }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record Response(
            String id,
            String nickname,
            String email,
            String profileImage
    ) {
    }
}
