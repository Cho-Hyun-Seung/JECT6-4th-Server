package com.ject6.boost.domain.blog.repository;

import com.ject6.boost.presentation.blog.dto.BloggerResponse;
import com.ject6.boost.presentation.blog.dto.RecommendedCampaignResponse;
import java.util.List;

public interface BlogRecommendationRepository {
    List<RecommendedCampaignResponse.CampaignItem> findRecommendedCampaigns(Long userId, Long analysisId, int limit);

    BloggerCandidates findBloggerCandidates(Long userId, Long analysisId, int limit);

    record BloggerCandidates(
            String category,
            List<BloggerResponse.BloggerItem> bloggers
    ) {}
}
