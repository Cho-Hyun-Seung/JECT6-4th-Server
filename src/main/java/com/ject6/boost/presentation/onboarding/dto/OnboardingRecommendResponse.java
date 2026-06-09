package com.ject6.boost.presentation.onboarding.dto;

import java.time.LocalDate;
import java.util.List;

public record OnboardingRecommendResponse(
        String sessionId,
        List<CampaignItem> campaigns
) {
    public record CampaignItem(Long id, String title, String category, String thumbnailUrl, LocalDate applyEndDate) {}
}
