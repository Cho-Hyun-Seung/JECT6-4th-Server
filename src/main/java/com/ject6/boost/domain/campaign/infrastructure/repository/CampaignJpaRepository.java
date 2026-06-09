package com.ject6.boost.domain.campaign.infrastructure.repository;

import com.ject6.boost.domain.campaign.domain.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.domain.constant.CampaignStatus;
import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignJpaRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByIdIn(List<Long> ids);

    List<Campaign> findTop3ByCategoryAndIdNotAndDeletedAtIsNull(
        CampaignCategory category, Long id);

    List<Campaign> findTop10ByDeletedAtIsNullOrderByViewCountDesc();

    List<Campaign> findTop10ByIsGuaranteedTrueAndDeletedAtIsNullOrderByApplyEndDateAsc();

    List<Campaign> findTop10ByStatusAndApplyEndDateAfterAndDeletedAtIsNullOrderByApplyEndDateAsc(
        CampaignStatus status, LocalDate now);
}