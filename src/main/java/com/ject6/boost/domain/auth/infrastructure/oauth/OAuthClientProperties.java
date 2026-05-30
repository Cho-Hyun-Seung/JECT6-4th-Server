package com.ject6.boost.domain.auth.infrastructure.oauth;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import com.ject6.boost.domain.auth.domain.OAuthProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth")
public class OAuthClientProperties {

    private Duration sessionTtl = Duration.ofDays(14);
    private Map<OAuthProvider, Provider> providers = new EnumMap<>(OAuthProvider.class);

    public Provider provider(OAuthProvider provider) {
        return providers.get(provider);
    }

    @Getter
    @Setter
    public static class Provider {

        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String tokenUri;
        private String userInfoUri;
    }
}
