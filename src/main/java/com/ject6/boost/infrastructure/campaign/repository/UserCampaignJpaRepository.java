package com.ject6.boost.infrastructure.campaign.repository;

import com.ject6.boost.domain.campaign.constant.UserCampaignStatus;
import com.ject6.boost.domain.campaign.entity.UserCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserCampaignJpaRepository extends JpaRepository<UserCampaign, Long> {
    Optional<UserCampaign> findByUserIdAndCampaignId(Long userId, Long campaignId);
    Optional<UserCampaign> findByUserIdAndCampaignIdAndStatus(Long userId, Long campaignId, UserCampaignStatus status);
    List<UserCampaign> findByUserId(Long userId);
    List<UserCampaign> findByUserIdAndStatus(Long userId, UserCampaignStatus status);
    List<UserCampaign> findByCampaignIdAndStatus(Long campaignId, UserCampaignStatus status);
    boolean existsByUserIdAndCampaignIdAndStatus(Long userId, Long campaignId, UserCampaignStatus status);
    long countByCampaignIdAndStatus(Long campaignId, UserCampaignStatus status);
}
