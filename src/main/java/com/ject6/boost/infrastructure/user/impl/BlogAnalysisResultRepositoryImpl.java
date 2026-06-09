package com.ject6.boost.infrastructure.user.impl;

import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.infrastructure.user.repository.BlogAnalysisResultJpaRepository;
import com.ject6.boost.domain.user.repository.BlogAnalysisResultRepository;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlogAnalysisResultRepositoryImpl implements BlogAnalysisResultRepository {

    private final BlogAnalysisResultJpaRepository blogAnalysisResultJpaRepository;

    @Override
    public int softDeleteByUser(User user, OffsetDateTime deletedAt) {
        return blogAnalysisResultJpaRepository.softDeleteByUser(user, deletedAt);
    }

    @Override
    public List<BlogAnalysisResult> findByUserIdAndDeletedAtIsNull(Long userId) {
        return blogAnalysisResultJpaRepository.findByUserIdAndDeletedAtIsNull(userId);
    }

    @Override
    public List<BlogAnalysisResult> findByUserIdInAndDeletedAtIsNull(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        return blogAnalysisResultJpaRepository.findByUserIdInAndDeletedAtIsNull(userIds);
    }
}
