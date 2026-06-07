package com.ject6.boost.domain.campaign.domain.repository;

import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
    Optional<Campaign> findActiveById(Long id);
    List<Campaign> findAllByIdIn(List<Long> ids);
    List<Campaign> findActiveByCategoryAndType(String category, String campaignType);
    List<Campaign> findActiveByCategory(String category);
    List<Campaign> findActiveFallback(int limit);
}
