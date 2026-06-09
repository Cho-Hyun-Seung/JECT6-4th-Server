package com.ject6.boost.domain.user.infrastructure.repository;

import com.ject6.boost.domain.user.domain.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.domain.entity.User;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlogAnalysisResultJpaRepository extends JpaRepository<BlogAnalysisResult, Long> {

    @Modifying
    @Query("""
            update BlogAnalysisResult result
            set result.deletedAt = :deletedAt
            where result.user = :user
              and result.deletedAt is null
            """)
    int softDeleteByUser(@Param("user") User user, @Param("deletedAt") OffsetDateTime deletedAt);

    List<BlogAnalysisResult> findByUserIdAndDeletedAtIsNull(Long userId);

    List<BlogAnalysisResult> findByUserIdInAndDeletedAtIsNull(List<Long> userIds);
}
