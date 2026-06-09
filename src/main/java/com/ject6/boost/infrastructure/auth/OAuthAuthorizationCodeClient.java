package com.ject6.boost.infrastructure.auth;

import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.application.auth.exception.AuthErrorCode;
import com.ject6.boost.domain.auth.OAuthProvider;
import com.ject6.boost.presentation.auth.dto.OAuthLoginRequest;
import com.ject6.boost.presentation.auth.dto.OAuthUserProfile;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class OAuthAuthorizationCodeClient {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RestClient restClient = RestClient.create();

    public OAuthUserProfile fetchProfile(OAuthProvider provider, OAuthLoginRequest request) {
        validateRequest(request);
        ClientRegistration registration = findRegistration(provider);
        String accessToken = requestAccessToken(registration, request);
        Map<String, Object> userInfo = requestUserInfo(registration, accessToken);
        return toProfile(provider, userInfo);
    }

    private void validateRequest(OAuthLoginRequest request) {
        if (request == null || !StringUtils.hasText(request.code())) {
            throw new BusinessException(AuthErrorCode.OAUTH_AUTHORIZATION_CODE_REQUIRED);
        }
        if (!StringUtils.hasText(request.redirectUri())) {
            throw new BusinessException(AuthErrorCode.OAUTH_REDIRECT_URI_REQUIRED);
        }
    }

    private ClientRegistration findRegistration(OAuthProvider provider) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(provider.registrationId());
        if (registration == null) {
            throw new BusinessException(AuthErrorCode.OAUTH_PROVIDER_CONFIGURATION_REQUIRED);
        }
        return registration;
    }

    private String requestAccessToken(ClientRegistration registration, OAuthLoginRequest request) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(OAuth2ParameterNames.GRANT_TYPE, "authorization_code");
        body.add(OAuth2ParameterNames.CODE, request.code().trim());
        body.add(OAuth2ParameterNames.REDIRECT_URI, request.redirectUri().trim());
        body.add(OAuth2ParameterNames.CLIENT_ID, registration.getClientId());
        if (StringUtils.hasText(registration.getClientSecret())) {
            body.add(OAuth2ParameterNames.CLIENT_SECRET, registration.getClientSecret());
        }

        try {
            Map<String, Object> tokenResponse = restClient.post()
                    .uri(registration.getProviderDetails().getTokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(MAP_RESPONSE_TYPE);

            String accessToken = stringValue(tokenResponse, OAuth2ParameterNames.ACCESS_TOKEN);
            if (!StringUtils.hasText(accessToken)) {
                throw new BusinessException(AuthErrorCode.OAUTH_ACCESS_TOKEN_REQUEST_FAILED);
            }
            return accessToken;
        } catch (RestClientException exception) {
            throw new BusinessException(AuthErrorCode.OAUTH_ACCESS_TOKEN_REQUEST_FAILED);
        }
    }

    private Map<String, Object> requestUserInfo(ClientRegistration registration, String accessToken) {
        try {
            Map<String, Object> userInfo = restClient.get()
                    .uri(registration.getProviderDetails().getUserInfoEndpoint().getUri())
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(MAP_RESPONSE_TYPE);

            if (userInfo == null || userInfo.isEmpty()) {
                throw new BusinessException(AuthErrorCode.OAUTH_USER_INFO_REQUEST_FAILED);
            }
            return userInfo;
        } catch (RestClientException exception) {
            throw new BusinessException(AuthErrorCode.OAUTH_USER_INFO_REQUEST_FAILED);
        }
    }

    private OAuthUserProfile toProfile(OAuthProvider provider, Map<String, Object> userInfo) {
        return switch (provider) {
            case KAKAO -> kakaoProfile(userInfo);
            case GOOGLE -> googleProfile(userInfo);
            case NAVER -> naverProfile(userInfo);
        };
    }

    private OAuthUserProfile kakaoProfile(Map<String, Object> userInfo) {
        String providerUserId = stringValue(userInfo, "id");
        Map<String, Object> kakaoAccount = mapValue(userInfo, "kakao_account");
        Map<String, Object> accountProfile = mapValue(kakaoAccount, "profile");
        Map<String, Object> properties = mapValue(userInfo, "properties");

        String nickname = firstText(
                stringValue(accountProfile, "nickname"),
                stringValue(properties, "nickname")
        );
        String profileImageUrl = firstText(
                stringValue(accountProfile, "profile_image_url"),
                stringValue(properties, "profile_image")
        );

        return profile(OAuthProvider.KAKAO, providerUserId, nickname, stringValue(kakaoAccount, "email"), profileImageUrl);
    }

    private OAuthUserProfile googleProfile(Map<String, Object> userInfo) {
        return profile(
                OAuthProvider.GOOGLE,
                stringValue(userInfo, "sub"),
                stringValue(userInfo, "name"),
                stringValue(userInfo, "email"),
                stringValue(userInfo, "picture")
        );
    }

    private OAuthUserProfile naverProfile(Map<String, Object> userInfo) {
        Map<String, Object> response = mapValue(userInfo, "response");
        return profile(
                OAuthProvider.NAVER,
                stringValue(response, "id"),
                stringValue(response, "nickname"),
                stringValue(response, "email"),
                stringValue(response, "profile_image")
        );
    }

    private OAuthUserProfile profile(
            OAuthProvider provider,
            String providerUserId,
            String nickname,
            String email,
            String profileImageUrl
    ) {
        if (!StringUtils.hasText(providerUserId)) {
            throw new BusinessException(AuthErrorCode.OAUTH_USER_ID_MISSING);
        }
        return new OAuthUserProfile(provider, providerUserId, nickname, email, profileImageUrl);
    }

    private String firstText(String first, String second) {
        return StringUtils.hasText(first) ? first : second;
    }

    private String stringValue(Map<String, Object> source, String key) {
        if (source == null) {
            return null;
        }
        Object value = source.get(key);
        return value == null ? null : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mapValue(Map<String, Object> source, String key) {
        if (source == null || !(source.get(key) instanceof Map<?, ?>)) {
            return Map.of();
        }
        return (Map<String, Object>) source.get(key);
    }
}
