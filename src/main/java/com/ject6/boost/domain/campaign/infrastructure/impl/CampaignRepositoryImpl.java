package com.ject6.boost.domain.campaign.infrastructure.impl;

import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import com.ject6.boost.domain.campaign.domain.repository.CampaignRepository;
import com.ject6.boost.domain.campaign.infrastructure.repository.CampaignJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepository {

    private static final int CANDIDATE_LIMIT = 20;

    private final CampaignJpaRepository jpaRepository;

    @Override
    public Optional<Campaign> findActiveById(Long id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public List<Campaign> findAllByIdIn(List<Long> ids) {
        return jpaRepository.findAllByIdInAndDeletedAtIsNull(ids);
    }

    @Override
    public List<Campaign> findActiveByCategoryAndType(String category, String campaignType) {
        return jpaRepository.findActiveByCategoryAndCampaignType(
                category, campaignType, LocalDate.now(), PageRequest.of(0, CANDIDATE_LIMIT));
    }

    @Override
    public List<Campaign> findActiveByCategory(String category) {
        return jpaRepository.findActiveByCategory(
                category, LocalDate.now(), PageRequest.of(0, CANDIDATE_LIMIT));
    }

    @Override
    public List<Campaign> findActiveFallback(int limit) {
        return jpaRepository.findActiveCampaigns(LocalDate.now(), PageRequest.of(0, limit));
    }
}
