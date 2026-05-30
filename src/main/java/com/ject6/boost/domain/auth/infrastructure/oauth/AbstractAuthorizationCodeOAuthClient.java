package com.ject6.boost.domain.auth.infrastructure.oauth;

import com.ject6.boost.domain.auth.application.dto.OAuthLoginRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

public abstract class AbstractAuthorizationCodeOAuthClient implements OAuthProviderClient {

    protected static final String BEARER_PREFIX = "Bearer ";

    private final OAuthClientProperties properties;
    private final RestClient restClient;

    protected AbstractAuthorizationCodeOAuthClient(OAuthClientProperties properties, RestClient restClient) {
        this.properties = properties;
        this.restClient = restClient;
    }

    protected OAuthTokenResponse requestToken(OAuthLoginRequest request) {
        OAuthClientProperties.Provider providerProperties = providerProperties();
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", requiredProperty(providerProperties.getClientId(), "client-id"));
        form.add("redirect_uri", resolveRedirectUri(request, providerProperties));
        form.add("code", request.code());

        addClientSecret(form, providerProperties);
        addProviderTokenParameters(form, request);

        OAuthTokenResponse response = restClient.post()
                .uri(requiredProperty(providerProperties.getTokenUri(), "token-uri"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(OAuthTokenResponse.class);

        if (response == null || !StringUtils.hasText(response.accessToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, provider().name() + " access token request failed.");
        }

        return response;
    }

    protected <T> T requestUserInfo(String accessToken, Class<T> responseType) {
        T response = restClient.get()
                .uri(requiredProperty(providerProperties().getUserInfoUri(), "user-info-uri"))
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessToken)
                .retrieve()
                .body(responseType);

        if (response == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, provider().name() + " user info request failed.");
        }

        return response;
    }

    protected OAuthClientProperties.Provider providerProperties() {
        OAuthClientProperties.Provider providerProperties = properties.provider(provider());

        if (providerProperties == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "oauth.providers." + providerKey() + " is required.");
        }

        return providerProperties;
    }

    protected void addClientSecret(LinkedMultiValueMap<String, String> form, OAuthClientProperties.Provider properties) {
        if (StringUtils.hasText(properties.getClientSecret())) {
            form.add("client_secret", properties.getClientSecret());
        }
    }

    protected void addProviderTokenParameters(LinkedMultiValueMap<String, String> form, OAuthLoginRequest request) {
    }

    protected String providerKey() {
        return provider().name().toLowerCase();
    }

    private String resolveRedirectUri(OAuthLoginRequest request, OAuthClientProperties.Provider providerProperties) {
        if (StringUtils.hasText(request.redirectUri())) {
            return request.redirectUri();
        }
        return requiredProperty(providerProperties.getRedirectUri(), "redirect-uri");
    }

    private String requiredProperty(String value, String propertyName) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "oauth.providers." + providerKey() + "." + propertyName + " is required."
            );
        }
        return value;
    }
}
