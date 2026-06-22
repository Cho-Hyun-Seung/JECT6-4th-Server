package com.ject6.boost.application.campaign.service;

import com.ject6.boost.application.campaign.exception.CampaignErrorCode;
import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.domain.campaign.entity.Campaign;
import com.ject6.boost.domain.campaign.entity.UserCampaignApply;
import com.ject6.boost.domain.campaign.repository.CampaignRepository;
import com.ject6.boost.domain.campaign.repository.UserCampaignApplyRepository;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.repository.UserRepository;
import com.ject6.boost.presentation.campaign.dto.CampaignApplyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignApplyService {

    private final CampaignRepository campaignRepository;
    private final UserCampaignApplyRepository userCampaignApplyRepository;
    private final UserRepository userRepository;

    @Transactional
    public CampaignApplyResponse apply(Long userId, Long campaignId) {
        Campaign campaign = campaignRepository.findActiveById(campaignId)
                .orElseThrow(() -> new BusinessException(CampaignErrorCode.CAMPAIGN_NOT_FOUND));

        UserCampaignApply apply = userCampaignApplyRepository
                .findByUserIdAndCampaignId(userId, campaignId)
                .orElseGet(() -> createApply(userId, campaign));

        return CampaignApplyResponse.from(apply);
    }

    private UserCampaignApply createApply(Long userId, Campaign campaign) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new BusinessException(CampaignErrorCode.CAMPAIGN_NOT_FOUND));
        return userCampaignApplyRepository.save(UserCampaignApply.create(user, campaign));
    }
}
