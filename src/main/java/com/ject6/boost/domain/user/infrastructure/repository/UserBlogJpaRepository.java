package com.ject6.boost.domain.user.infrastructure.repository;

import com.ject6.boost.domain.user.domain.constant.BlogPlatform;
import com.ject6.boost.domain.user.domain.constant.BlogStatus;
import com.ject6.boost.domain.user.domain.entity.User;
import com.ject6.boost.domain.user.domain.entity.UserBlog;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBlogJpaRepository extends JpaRepository<UserBlog, Long> {

    Optional<UserBlog> findByUserAndPlatformAndStatusAndDeletedAtIsNull(
            User user,
            BlogPlatform platform,
            BlogStatus status
    );

    List<UserBlog> findByUserAndStatusAndDeletedAtIsNull(User user, BlogStatus status);

    @Modifying
    @Query("""
            update UserBlog blog
            set blog.status = :status,
                blog.deletedAt = :deletedAt
            where blog.user = :user
              and blog.deletedAt is null
            """)
    int softDeleteByUser(
            @Param("user") User user,
            @Param("status") BlogStatus status,
            @Param("deletedAt") OffsetDateTime deletedAt
    );
}
