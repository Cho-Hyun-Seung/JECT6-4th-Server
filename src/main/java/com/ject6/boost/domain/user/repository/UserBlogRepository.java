package com.ject6.boost.domain.user.repository;

import com.ject6.boost.domain.user.constant.BlogPlatform;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.entity.UserBlog;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface UserBlogRepository {

    Optional<UserBlog> findActiveByUserAndPlatform(User user, BlogPlatform platform);

    List<UserBlog> findActiveByUser(User user);

    UserBlog save(UserBlog blog);

    int softDeleteByUser(User user, OffsetDateTime deletedAt);
}
