package com.ject6.boost.domain.campaign.repository;

import com.ject6.boost.domain.campaign.entity.UserCampaignLike;
import java.util.List;
import java.util.Optional;

public interface UserCampaignLikeRepository {
    UserCampaignLike save(UserCampaignLike like);
    void delete(UserCampaignLike like);
    Optional<UserCampaignLike> findByUserIdAndCampaignId(Long userId, Long campaignId);
    List<UserCampaignLike> findByUserId(Long userId);
    List<UserCampaignLike> findByCampaignId(Long campaignId);
    List<Long> findCampaignIdsByUserIdAndCampaignIdIn(Long userId, List<Long> campaignIds);
    boolean existsByUserIdAndCampaignId(Long userId, Long campaignId);
    long countByCampaignId(Long campaignId);
}
