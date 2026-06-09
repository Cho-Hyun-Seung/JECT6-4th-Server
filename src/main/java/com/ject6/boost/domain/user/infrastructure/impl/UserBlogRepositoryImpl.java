package com.ject6.boost.domain.user.infrastructure.impl;

import com.ject6.boost.domain.user.domain.constant.BlogPlatform;
import com.ject6.boost.domain.user.domain.constant.BlogStatus;
import com.ject6.boost.domain.user.domain.entity.User;
import com.ject6.boost.domain.user.domain.entity.UserBlog;
import com.ject6.boost.domain.user.domain.repository.UserBlogRepository;
import com.ject6.boost.domain.user.infrastructure.repository.UserBlogJpaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserBlogRepositoryImpl implements UserBlogRepository {

    private final UserBlogJpaRepository userBlogJpaRepository;

    @Override
    public Optional<UserBlog> findActiveByUserAndPlatform(User user, BlogPlatform platform) {
        return userBlogJpaRepository.findByUserAndPlatformAndStatusAndDeletedAtIsNull(
                user,
                platform,
                BlogStatus.ACTIVE
        );
    }

    @Override
    public List<UserBlog> findActiveByUser(User user) {
        return userBlogJpaRepository.findByUserAndStatusAndDeletedAtIsNull(user, BlogStatus.ACTIVE);
    }

    @Override
    public UserBlog save(UserBlog blog) {
        return userBlogJpaRepository.save(blog);
    }

    @Override
    public int softDeleteByUser(User user, OffsetDateTime deletedAt) {
        return userBlogJpaRepository.softDeleteByUser(user, BlogStatus.INACTIVE, deletedAt);
    }
}
