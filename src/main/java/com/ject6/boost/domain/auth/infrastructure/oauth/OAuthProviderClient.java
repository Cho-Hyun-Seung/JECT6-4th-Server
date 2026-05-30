package com.ject6.boost.domain.auth.infrastructure.oauth;

import com.ject6.boost.domain.auth.application.dto.OAuthLoginRequest;
import com.ject6.boost.domain.auth.application.dto.OAuthUserProfile;
import com.ject6.boost.domain.auth.domain.OAuthProvider;

public interface OAuthProviderClient {

    OAuthProvider provider();

    OAuthUserProfile fetchUserProfile(OAuthLoginRequest request);
}
