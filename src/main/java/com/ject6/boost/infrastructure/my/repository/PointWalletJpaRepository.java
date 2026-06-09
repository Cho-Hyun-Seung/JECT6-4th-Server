package com.ject6.boost.infrastructure.my.repository;

import com.ject6.boost.domain.my.entity.PointWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PointWalletJpaRepository extends JpaRepository<PointWallet, Long> {
    Optional<PointWallet> findByUserId(Long userId);
}
