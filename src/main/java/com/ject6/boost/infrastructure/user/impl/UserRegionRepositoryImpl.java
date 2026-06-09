package com.ject6.boost.infrastructure.user.impl;

import com.ject6.boost.domain.user.entity.Region;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.entity.UserRegion;
import com.ject6.boost.infrastructure.user.repository.UserRegionJpaRepository;
import com.ject6.boost.domain.user.repository.UserRegionRepository;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRegionRepositoryImpl implements UserRegionRepository {

    private final UserRegionJpaRepository userRegionJpaRepository;

    @Override
    public List<UserRegion> findByUser(User user) {
        return userRegionJpaRepository.findByUser(user);
    }

    @Override
    public void replaceAll(User user, List<Region> regions) {
        userRegionJpaRepository.deleteByUser(user);
        userRegionJpaRepository.saveAll(regions.stream()
                .map(region -> UserRegion.create(user, region))
                .toList());
    }

    @Override
    public int softDeleteByUser(User user, OffsetDateTime deletedAt) {
        return userRegionJpaRepository.softDeleteByUser(user, deletedAt);
    }
}
