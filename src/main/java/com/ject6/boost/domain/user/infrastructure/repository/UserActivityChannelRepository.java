package com.ject6.boost.domain.user.infrastructure.repository;

import com.ject6.boost.domain.user.domain.constant.ActivityType;
import com.ject6.boost.domain.user.domain.entity.User;
import com.ject6.boost.domain.user.domain.entity.UserActivityChannel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityChannelRepository extends JpaRepository<UserActivityChannel, Long> {

    List<UserActivityChannel> findByUser(User user);

    Optional<UserActivityChannel> findByUserAndActivityType(User user, ActivityType activityType);

    void deleteByUserAndActivityType(User user, ActivityType activityType);
}
