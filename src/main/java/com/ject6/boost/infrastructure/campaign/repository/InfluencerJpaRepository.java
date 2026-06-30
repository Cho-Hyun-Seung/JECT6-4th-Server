package com.ject6.boost.infrastructure.campaign.repository;

import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.entity.Influencer;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfluencerJpaRepository extends JpaRepository<Influencer, Long> {

    List<Influencer> findByCategory(CampaignCategory category);
}
