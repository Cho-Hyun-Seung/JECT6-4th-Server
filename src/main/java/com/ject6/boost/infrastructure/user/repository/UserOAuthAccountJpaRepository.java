package com.ject6.boost.infrastructure.user.repository;

import com.ject6.boost.domain.auth.OAuthProvider;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.entity.UserOAuthAccount;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserOAuthAccountJpaRepository extends JpaRepository<UserOAuthAccount, Long> {

    @Query("""
            select account
            from UserOAuthAccount account
            join account.user user
            where account.provider = :provider
              and account.providerUserId = :providerUserId
              and account.deletedAt is null
              and user.deletedAt is null
            """)
    Optional<UserOAuthAccount> findActiveByProviderAndProviderUserId(
            @Param("provider") OAuthProvider provider,
            @Param("providerUserId") String providerUserId
    );

    @Modifying
    @Query("""
            update UserOAuthAccount account
            set account.deletedAt = :deletedAt
            where account.provider = :provider
              and account.providerUserId = :providerUserId
              and account.deletedAt is null
              and account.user.deletedAt is not null
            """)
    int softDeleteActiveAccountOfDeletedUser(
            @Param("provider") OAuthProvider provider,
            @Param("providerUserId") String providerUserId,
            @Param("deletedAt") OffsetDateTime deletedAt
    );

    @Modifying
    @Query("""
            update UserOAuthAccount account
            set account.deletedAt = :deletedAt
            where account.user = :user
              and account.deletedAt is null
            """)
    int softDeleteByUser(@Param("user") User user, @Param("deletedAt") OffsetDateTime deletedAt);
}
