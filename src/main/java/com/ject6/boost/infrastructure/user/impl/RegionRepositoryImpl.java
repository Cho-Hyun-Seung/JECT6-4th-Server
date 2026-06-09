package com.ject6.boost.infrastructure.user.impl;

import com.ject6.boost.domain.user.entity.Region;
import com.ject6.boost.infrastructure.user.repository.RegionJpaRepository;
import com.ject6.boost.domain.user.repository.RegionRepository;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RegionRepositoryImpl implements RegionRepository {

    private final RegionJpaRepository regionJpaRepository;

    @Override
    public List<Region> findByIdIn(Collection<Long> ids) {
        return regionJpaRepository.findByIdIn(ids);
    }
}
