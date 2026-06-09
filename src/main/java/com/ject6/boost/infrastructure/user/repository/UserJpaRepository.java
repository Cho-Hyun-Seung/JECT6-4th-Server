package com.ject6.boost.infrastructure.user.repository;

import com.ject6.boost.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNicknameAndDeletedAtIsNull(String nickname);
}
