package com.ject6.boost.domain.campaign.presentation.dto;

import java.util.List;

public record LikeAnalysisResponse(
        Long campaignId,
        long likeCount,
        boolean analyzed,
        List<CategoryStat> topCategories,
        List<String> topKeywords
) {
    public record CategoryStat(String category, int count) {}

    public static LikeAnalysisResponse insufficient(Long campaignId, long likeCount) {
        return new LikeAnalysisResponse(campaignId, likeCount, false, List.of(), List.of());
    }
}
