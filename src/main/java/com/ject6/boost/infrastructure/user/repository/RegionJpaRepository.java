package com.ject6.boost.infrastructure.user.repository;

import com.ject6.boost.domain.user.entity.Region;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionJpaRepository extends JpaRepository<Region, Long> {

    List<Region> findByIdIn(Collection<Long> ids);
}
