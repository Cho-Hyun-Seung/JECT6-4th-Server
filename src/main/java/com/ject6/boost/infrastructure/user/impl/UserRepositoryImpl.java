package com.ject6.boost.infrastructure.user.impl;

import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.infrastructure.user.repository.UserJpaRepository;
import com.ject6.boost.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findActiveById(Long id) {
        return userJpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public boolean existsByNicknameAndDeletedAtIsNull(String nickname) {
        return userJpaRepository.existsByNicknameAndDeletedAtIsNull(nickname);
    }
}
