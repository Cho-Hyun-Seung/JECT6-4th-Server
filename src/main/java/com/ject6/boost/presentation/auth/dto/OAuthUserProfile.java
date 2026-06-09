package com.ject6.boost.presentation.auth.dto;

import com.ject6.boost.domain.auth.OAuthProvider;

public record OAuthUserProfile(
        OAuthProvider provider,
        String providerUserId,
        String nickname,
        String email,
        String profileImageUrl
) {
}
