package com.ject6.boost.domain.auth.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ject6.boost.domain.auth.application.dto.OAuthLoginRequest;
import com.ject6.boost.domain.auth.application.dto.OAuthLoginResponse;
import com.ject6.boost.domain.auth.application.dto.OAuthUserProfile;
import com.ject6.boost.domain.auth.domain.OAuthProvider;
import com.ject6.boost.domain.auth.infrastructure.OAuthRedisKeys;
import com.ject6.boost.domain.auth.infrastructure.oauth.OAuthClientProperties;
import com.ject6.boost.domain.auth.infrastructure.oauth.OAuthProviderClient;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final OAuthClientProperties oauthClientProperties;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final List<OAuthProviderClient> providerClients;

    private Map<OAuthProvider, OAuthProviderClient> clients;

    @PostConstruct
    void initializeClients() {
        this.clients = buildClientMap(providerClients);
    }

    public OAuthLoginResponse login(String providerValue, OAuthLoginRequest request) {
        if (request == null || !StringUtils.hasText(request.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OAuth authorization code is required.");
        }
        OAuthProvider provider = OAuthProvider.from(providerValue);

        OAuthProviderClient client = clients.get(provider);

        if (client == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported OAuth provider: " + provider.name());
        }

        OAuthUserProfile userProfile = client.fetchUserProfile(request);
        String accessToken = issueSessionToken(userProfile);

        return new OAuthLoginResponse(
                accessToken,
                TOKEN_TYPE,
                oauthClientProperties.getSessionTtl().toSeconds(),
                userProfile
        );
    }

    private Map<OAuthProvider, OAuthProviderClient> buildClientMap(List<OAuthProviderClient> clients) {
        EnumMap<OAuthProvider, OAuthProviderClient> clientMap = new EnumMap<>(OAuthProvider.class);

        for (OAuthProviderClient client : clients) {
            OAuthProviderClient previous = clientMap.put(client.provider(), client);

            if (previous != null) {
                throw new IllegalStateException("Duplicated OAuth provider client: " + client.provider());
            }
        }

        return Collections.unmodifiableMap(clientMap);
    }

    private String issueSessionToken(OAuthUserProfile userProfile) {
        String sessionToken = UUID.randomUUID().toString();
        String redisKey = OAuthRedisKeys.SESSION_KEY_PREFIX + sessionToken;

        try {
            redisTemplate.opsForValue().set(
                    redisKey,
                    objectMapper.writeValueAsString(userProfile),
                    oauthClientProperties.getSessionTtl()
            );
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to issue login token.", exception);
        }

        return sessionToken;
    }
}
