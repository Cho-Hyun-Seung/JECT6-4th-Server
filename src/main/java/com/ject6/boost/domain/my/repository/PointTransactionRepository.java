package com.ject6.boost.domain.my.repository;

import com.ject6.boost.domain.my.entity.PointTransaction;
import java.util.List;

public interface PointTransactionRepository {
    PointTransaction save(PointTransaction transaction);
    List<PointTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}
