package com.ject6.boost.domain.auth.application.dto;

import com.ject6.boost.domain.auth.domain.OAuthProvider;

public record OAuthUserProfile(
        OAuthProvider provider,
        String providerUserId,
        String nickname,
        String email,
        String profileImageUrl
) {
}
