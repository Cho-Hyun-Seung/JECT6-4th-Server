package com.ject6.boost.domain.user.domain.repository;

import com.ject6.boost.domain.user.domain.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.domain.entity.User;
import java.time.OffsetDateTime;
import java.util.List;

public interface BlogAnalysisResultRepository {

    int softDeleteByUser(User user, OffsetDateTime deletedAt);

    List<BlogAnalysisResult> findByUserIdAndDeletedAtIsNull(Long userId);

    List<BlogAnalysisResult> findByUserIdInAndDeletedAtIsNull(List<Long> userIds);
}
