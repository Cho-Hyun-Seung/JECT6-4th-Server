package com.ject6.boost.domain.user.domain.repository;

import com.ject6.boost.domain.user.domain.entity.Region;
import com.ject6.boost.domain.user.domain.entity.User;
import com.ject6.boost.domain.user.domain.entity.UserRegion;
import java.time.OffsetDateTime;
import java.util.List;

public interface UserRegionRepository {

    List<UserRegion> findByUser(User user);

    void replaceAll(User user, List<Region> regions);

    int softDeleteByUser(User user, OffsetDateTime deletedAt);
}
