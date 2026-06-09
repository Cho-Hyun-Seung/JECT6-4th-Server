package com.ject6.boost.domain.user.repository;

import com.ject6.boost.domain.auth.OAuthProvider;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.entity.UserOAuthAccount;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserOAuthAccountRepository {

    Optional<UserOAuthAccount> findActiveByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

    UserOAuthAccount save(UserOAuthAccount userOAuthAccount);

    int softDeleteByUser(User user, OffsetDateTime deletedAt);
}
