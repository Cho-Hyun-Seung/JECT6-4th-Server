package com.ject6.boost.infrastructure.campaign.repository;

import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.domain.campaign.entity.UserCampaignApply;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCampaignApplyJpaRepository extends JpaRepository<UserCampaignApply, Long> {
    Optional<UserCampaignApply> findByUserIdAndCampaignId(Long userId, Long campaignId);
    List<UserCampaignApply> findByUserId(Long userId);
    List<UserCampaignApply> findByUserIdAndStatus(Long userId, CampaignApplyStatus status);
}
