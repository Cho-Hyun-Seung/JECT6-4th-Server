package com.ject6.boost.domain.user.infrastructure.impl;

import com.ject6.boost.domain.user.domain.constant.CategoryType;
import com.ject6.boost.domain.user.domain.entity.User;
import com.ject6.boost.domain.user.domain.entity.UserCategory;
import com.ject6.boost.domain.user.domain.repository.UserCategoryRepository;
import com.ject6.boost.domain.user.infrastructure.repository.UserCategoryJpaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCategoryRepositoryImpl implements UserCategoryRepository {

    private final UserCategoryJpaRepository userCategoryJpaRepository;

    @Override
    public List<UserCategory> findByUser(User user) {
        return userCategoryJpaRepository.findByUser(user);
    }

    @Override
    public List<UserCategory> saveAll(List<UserCategory> userCategories) {
        return userCategoryJpaRepository.saveAll(userCategories);
    }

    @Override
    public void replaceAll(User user, List<CategoryType> categoryTypes) {
        userCategoryJpaRepository.deleteByUser(user);
        userCategoryJpaRepository.saveAll(categoryTypes.stream()
                .map(categoryType -> UserCategory.create(user, categoryType))
                .toList());
    }

    @Override
    public void deleteByUser(User user) {
        userCategoryJpaRepository.deleteByUser(user);
    }

    @Override
    public int softDeleteByUser(User user, OffsetDateTime deletedAt) {
        return userCategoryJpaRepository.softDeleteByUser(user, deletedAt);
    }
}