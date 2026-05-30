package com.ject6.boost.domain.auth.domain;

import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

public enum OAuthProvider {
    KAKAO,
    GOOGLE,
    NAVER;

    public static OAuthProvider from(String value) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OAuth provider is required.");
        }

        try {
            return OAuthProvider.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported OAuth provider: " + value);
        }
    }
}
