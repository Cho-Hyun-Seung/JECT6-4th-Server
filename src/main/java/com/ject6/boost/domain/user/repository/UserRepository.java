package com.ject6.boost.domain.user.repository;

import com.ject6.boost.domain.user.entity.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findActiveById(Long id);

    User save(User user);

    boolean existsByNicknameAndDeletedAtIsNull(String nickname);
}
