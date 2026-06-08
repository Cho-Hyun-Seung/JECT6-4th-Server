package com.ject6.boost.domain.user.infrastructure.repository;

import com.ject6.boost.domain.user.domain.entity.User;
import com.ject6.boost.domain.user.domain.entity.UserRegion;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRegionJpaRepository extends JpaRepository<UserRegion, Long> {

    List<UserRegion> findByUser(User user);

    void deleteByUser(User user);

    @Modifying
    @Query("""
            update UserRegion userRegion
            set userRegion.deletedAt = :deletedAt
            where userRegion.user = :user
              and userRegion.deletedAt is null
            """)
    int softDeleteByUser(@Param("user") User user, @Param("deletedAt") OffsetDateTime deletedAt);
}
