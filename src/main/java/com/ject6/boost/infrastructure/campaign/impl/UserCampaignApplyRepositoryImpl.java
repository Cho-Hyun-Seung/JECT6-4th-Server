package com.ject6.boost.infrastructure.campaign.impl;

import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.domain.campaign.entity.UserCampaignApply;
import com.ject6.boost.domain.campaign.repository.UserCampaignApplyRepository;
import com.ject6.boost.infrastructure.campaign.repository.UserCampaignApplyJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCampaignApplyRepositoryImpl implements UserCampaignApplyRepository {

    private final UserCampaignApplyJpaRepository jpaRepository;

    @Override public UserCampaignApply save(UserCampaignApply apply) { return jpaRepository.save(apply); }
    @Override public Optional<UserCampaignApply> findById(Long id) { return jpaRepository.findById(id); }
    @Override public Optional<UserCampaignApply> findByUserIdAndCampaignId(Long userId, Long campaignId) { return jpaRepository.findByUserIdAndCampaignId(userId, campaignId); }
    @Override public List<UserCampaignApply> findByUserId(Long userId) { return jpaRepository.findByUserId(userId); }
    @Override public List<UserCampaignApply> findByUserIdAndStatus(Long userId, CampaignApplyStatus status) { return jpaRepository.findByUserIdAndStatus(userId, status); }
}
