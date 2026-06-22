package com.ject6.boost.domain.campaign.repository;

import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.domain.campaign.entity.UserCampaignApply;
import java.util.List;
import java.util.Optional;

public interface UserCampaignApplyRepository {
    UserCampaignApply save(UserCampaignApply apply);
    Optional<UserCampaignApply> findById(Long id);
    Optional<UserCampaignApply> findByUserIdAndCampaignId(Long userId, Long campaignId);
    List<UserCampaignApply> findByUserId(Long userId);
    List<UserCampaignApply> findByUserIdAndStatus(Long userId, CampaignApplyStatus status);
}
