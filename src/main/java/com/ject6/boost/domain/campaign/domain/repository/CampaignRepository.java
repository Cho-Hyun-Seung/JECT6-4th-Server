package com.ject6.boost.domain.campaign.domain.repository;

import com.ject6.boost.domain.campaign.domain.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.domain.constant.CampaignType;
import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignFilterRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampaignRepository {

    Optional<Campaign> findById(Long id);

    Optional<Campaign> findActiveById(Long id);

    List<Campaign> findAllByIdIn(List<Long> ids);

    Page<Campaign> search(CampaignFilterRequest filter, Pageable pageable);

    Page<Campaign> searchByKeyword(String keyword, Pageable pageable);

    List<Campaign> findRelated(Long campaignId, CampaignCategory category, int limit);

    List<Campaign> findPopular(int limit);

    List<Campaign> findGuaranteed(int limit);

    List<Campaign> findClosingSoon(int limit);

    List<Campaign> findActiveByCategoryAndType(CampaignCategory category, CampaignType type);

    List<Campaign> findActiveByCategory(CampaignCategory category);

    List<Campaign> findActiveFallback(int limit);
}