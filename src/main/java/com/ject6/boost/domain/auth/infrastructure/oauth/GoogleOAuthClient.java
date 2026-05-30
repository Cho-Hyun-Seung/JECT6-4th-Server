package com.ject6.boost.domain.auth.infrastructure.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ject6.boost.domain.auth.application.dto.OAuthLoginRequest;
import com.ject6.boost.domain.auth.application.dto.OAuthUserProfile;
import com.ject6.boost.domain.auth.domain.OAuthProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GoogleOAuthClient extends AbstractAuthorizationCodeOAuthClient {

    public GoogleOAuthClient(OAuthClientProperties properties, RestClient.Builder restClientBuilder) {
        super(properties, restClientBuilder.build());
    }

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public OAuthUserProfile fetchUserProfile(OAuthLoginRequest request) {
        OAuthTokenResponse token = requestToken(request);
        GoogleUserInfoResponse userInfo = requestUserInfo(token.accessToken(), GoogleUserInfoResponse.class);
        return userInfo.toUserProfile();
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record GoogleUserInfoResponse(
            String id,
            String email,
            String name,
            String picture,
            Boolean verifiedEmail
    ) {

        OAuthUserProfile toUserProfile() {
            if (id == null) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "GOOGLE user id is missing."
                );
            }

            return new OAuthUserProfile(OAuthProvider.GOOGLE, id, name, email, picture);
        }
    }
}
