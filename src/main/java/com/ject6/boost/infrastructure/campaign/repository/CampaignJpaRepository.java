package com.ject6.boost.infrastructure.campaign.repository;

import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.constant.CampaignStatus;
import com.ject6.boost.domain.campaign.entity.Campaign;
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