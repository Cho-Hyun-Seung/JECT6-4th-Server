package com.ject6.boost.domain.campaign.domain.repository;

import com.ject6.boost.domain.campaign.domain.constant.UserCampaignStatus;
import com.ject6.boost.domain.campaign.domain.entity.UserCampaign;
import java.util.List;
import java.util.Optional;

public interface UserCampaignRepository {
    UserCampaign save(UserCampaign userCampaign);
    void delete(UserCampaign userCampaign);
    Optional<UserCampaign> findById(Long id);
    Optional<UserCampaign> findByUserIdAndCampaignId(Long userId, Long campaignId);
    Optional<UserCampaign> findByUserIdAndCampaignIdAndStatus(Long userId, Long campaignId, UserCampaignStatus status);
    List<UserCampaign> findByUserId(Long userId);
    List<UserCampaign> findByUserIdAndStatus(Long userId, UserCampaignStatus status);
    List<UserCampaign> findByCampaignIdAndStatus(Long campaignId, UserCampaignStatus status);
    boolean existsByUserIdAndCampaignIdAndStatus(Long userId, Long campaignId, UserCampaignStatus status);
    long countByCampaignIdAndStatus(Long campaignId, UserCampaignStatus status);
}
