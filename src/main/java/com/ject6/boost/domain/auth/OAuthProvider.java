package com.ject6.boost.domain.auth;

import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.application.auth.exception.AuthErrorCode;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public enum OAuthProvider {
    KAKAO("kakao"),
    GOOGLE("google"),
    NAVER("naver");

    private final String registrationId;

    /**
     * Spring Security OAuth2 client registration id를 반환하는 함수.
     */
    public String registrationId() {
        return registrationId;
    }

    /**
     * OAuth2 client registration id로 서비스 OAuth provider enum을 찾는 함수.
     */
    public static OAuthProvider fromRegistrationId(String registrationId) {
        if (!StringUtils.hasText(registrationId)) {
            throw new BusinessException(AuthErrorCode.OAUTH_PROVIDER_REQUIRED);
        }

        for (OAuthProvider provider : values()) {
            if (provider.registrationId.equalsIgnoreCase(registrationId)) {
                return provider;
            }
        }

        throw new BusinessException(AuthErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
    }

    /**
     * API path 등 외부 입력 문자열로 서비스 OAuth provider enum을 찾는 함수.
     */
    public static OAuthProvider from(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(AuthErrorCode.OAUTH_PROVIDER_REQUIRED);
        }

        try {
            return OAuthProvider.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(AuthErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }
    }
}
