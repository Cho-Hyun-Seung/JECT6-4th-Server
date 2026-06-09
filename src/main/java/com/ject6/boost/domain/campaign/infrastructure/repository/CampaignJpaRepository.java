package com.ject6.boost.domain.campaign.infrastructure.repository;

import com.ject6.boost.domain.campaign.domain.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.domain.constant.CampaignStatus;
import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignJpaRepository extends JpaRepository<Campaign, Long> {

    // 관련 공고 (동일 카테고리, 해당 id 제외)
    List<Campaign> findTop3ByCategoryAndIdNotAndDeletedAtIsNull(
        CampaignCategory category, Long id);

    // 인기 (조회수 기준)
    List<Campaign> findTop10ByDeletedAtIsNullOrderByViewCountDesc();

    // 100% 당첨
    List<Campaign> findTop10ByIsGuaranteedTrueAndDeletedAtIsNullOrderByApplyEndDateAsc();

    // 마감 임박 (ACTIVE 상태 + 마감일 오늘 이후)
    List<Campaign> findTop10ByStatusAndApplyEndDateAfterAndDeletedAtIsNullOrderByApplyEndDateAsc(
        CampaignStatus status, LocalDate now);
}