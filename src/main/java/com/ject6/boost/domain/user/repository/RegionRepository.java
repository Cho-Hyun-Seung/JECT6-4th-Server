package com.ject6.boost.domain.user.repository;

import com.ject6.boost.domain.user.entity.Region;
import java.util.Collection;
import java.util.List;

public interface RegionRepository {

    List<Region> findByIdIn(Collection<Long> ids);
}
