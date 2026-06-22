package com.ject6.boost.infrastructure.campaign.impl;

import com.ject6.boost.domain.campaign.entity.UserCampaignLike;
import com.ject6.boost.domain.campaign.repository.UserCampaignLikeRepository;
import com.ject6.boost.infrastructure.campaign.repository.UserCampaignLikeJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCampaignLikeRepositoryImpl implements UserCampaignLikeRepository {

    private final UserCampaignLikeJpaRepository jpaRepository;

    @Override public UserCampaignLike save(UserCampaignLike like) { return jpaRepository.save(like); }
    @Override public void delete(UserCampaignLike like) { jpaRepository.delete(like); }
    @Override public Optional<UserCampaignLike> findByUserIdAndCampaignId(Long userId, Long campaignId) { return jpaRepository.findByUserIdAndCampaignId(userId, campaignId); }
    @Override public List<UserCampaignLike> findByUserId(Long userId) { return jpaRepository.findByUserId(userId); }
    @Override public List<UserCampaignLike> findByCampaignId(Long campaignId) { return jpaRepository.findByCampaignId(campaignId); }
    @Override public List<Long> findCampaignIdsByUserIdAndCampaignIdIn(Long userId, List<Long> campaignIds) { return jpaRepository.findCampaignIdsByUserIdAndCampaignIdIn(userId, campaignIds); }
    @Override public boolean existsByUserIdAndCampaignId(Long userId, Long campaignId) { return jpaRepository.existsByUserIdAndCampaignId(userId, campaignId); }
    @Override public long countByCampaignId(Long campaignId) { return jpaRepository.countByCampaignId(campaignId); }
}
