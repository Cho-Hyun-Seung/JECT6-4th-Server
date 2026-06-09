package com.ject6.boost.domain.my.repository;

import com.ject6.boost.domain.my.entity.PointWallet;
import java.util.Optional;

public interface PointWalletRepository {
    Optional<PointWallet> findByUserId(Long userId);
    PointWallet save(PointWallet wallet);
}
