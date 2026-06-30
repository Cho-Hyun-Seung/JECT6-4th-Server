package com.ject6.boost.infrastructure.campaign.impl;

import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.entity.Influencer;
import com.ject6.boost.domain.campaign.repository.InfluencerRepository;
import com.ject6.boost.infrastructure.campaign.repository.InfluencerJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InfluencerRepositoryImpl implements InfluencerRepository {

    private final InfluencerJpaRepository jpaRepository;

    @Override
    public List<Influencer> findFeedStories(CampaignCategory category) {

        if (category == null) {
            return jpaRepository.findAll();
        }

        return jpaRepository.findByCategory(category);
    }
}
