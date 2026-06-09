package com.ject6.boost.domain.user.repository;

import com.ject6.boost.domain.user.entity.Region;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.entity.UserRegion;
import java.time.OffsetDateTime;
import java.util.List;

public interface UserRegionRepository {

    List<UserRegion> findByUser(User user);

    void replaceAll(User user, List<Region> regions);

    int softDeleteByUser(User user, OffsetDateTime deletedAt);
}
