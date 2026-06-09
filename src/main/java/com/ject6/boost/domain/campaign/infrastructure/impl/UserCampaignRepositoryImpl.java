package com.ject6.boost.domain.campaign.infrastructure.impl;

import com.ject6.boost.domain.campaign.domain.constant.UserCampaignStatus;
import com.ject6.boost.domain.campaign.domain.entity.UserCampaign;
import com.ject6.boost.domain.campaign.domain.repository.UserCampaignRepository;
import com.ject6.boost.domain.campaign.infrastructure.repository.UserCampaignJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCampaignRepositoryImpl implements UserCampaignRepository {

    private final UserCampaignJpaRepository jpaRepository;

    @Override public UserCampaign save(UserCampaign uc)                             { return jpaRepository.save(uc); }
    @Override public void delete(UserCampaign uc)                                   { jpaRepository.delete(uc); }
    @Override public Optional<UserCampaign> findById(Long id)                       { return jpaRepository.findById(id); }
    @Override public Optional<UserCampaign> findByUserIdAndCampaignId(Long u, Long c) { return jpaRepository.findByUserIdAndCampaignId(u, c); }
    @Override public Optional<UserCampaign> findByUserIdAndCampaignIdAndStatus(Long u, Long c, UserCampaignStatus s) { return jpaRepository.findByUserIdAndCampaignIdAndStatus(u, c, s); }
    @Override public List<UserCampaign> findByUserId(Long userId)                   { return jpaRepository.findByUserId(userId); }
    @Override public List<UserCampaign> findByUserIdAndStatus(Long u, UserCampaignStatus s) { return jpaRepository.findByUserIdAndStatus(u, s); }
    @Override public List<UserCampaign> findByCampaignIdAndStatus(Long c, UserCampaignStatus s) { return jpaRepository.findByCampaignIdAndStatus(c, s); }
    @Override public boolean existsByUserIdAndCampaignIdAndStatus(Long u, Long c, UserCampaignStatus s) { return jpaRepository.existsByUserIdAndCampaignIdAndStatus(u, c, s); }
    @Override public long countByCampaignIdAndStatus(Long c, UserCampaignStatus s)  { return jpaRepository.countByCampaignIdAndStatus(c, s); }
}
