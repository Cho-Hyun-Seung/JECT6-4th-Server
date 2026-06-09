package com.ject6.boost.domain.campaign.application.service;

import com.ject6.boost.common.exception.BusinessException;
import com.ject6.boost.common.redis.ViewerCountService;
import com.ject6.boost.domain.campaign.application.exception.CampaignErrorCode;
import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import com.ject6.boost.domain.campaign.domain.repository.CampaignRepository;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignDetailResponse;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignFilterRequest;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ViewerCountService viewerCountService;

    public Page<CampaignListResponse> getCampaigns(
        CampaignFilterRequest filter, Pageable pageable) {
        return campaignRepository.search(filter, pageable)
            .map(CampaignListResponse::from);
    }

    public CampaignDetailResponse getCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
                CampaignErrorCode.CAMPAIGN_NOT_FOUND));
        return CampaignDetailResponse.from(campaign);
    }

    public Long getViewerCount(Long id) {
        return viewerCountService.getCount(id);
    }

    public List<CampaignListResponse> getRelated(Long id) {
        Campaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
                CampaignErrorCode.CAMPAIGN_NOT_FOUND));
        return campaignRepository.findRelated(id, campaign.getCategory(), 3)
            .stream()
            .map(CampaignListResponse::from)
            .toList();
    }

    public List<CampaignListResponse> getPopular() {
        return campaignRepository.findPopular(10)
            .stream()
            .map(CampaignListResponse::from)
            .toList();
    }

    public List<CampaignListResponse> getGuaranteed() {
        return campaignRepository.findGuaranteed(10)
            .stream()
            .map(CampaignListResponse::from)
            .toList();
    }

    public List<CampaignListResponse> getClosingSoon() {
        return campaignRepository.findClosingSoon(10)
            .stream()
            .map(CampaignListResponse::from)
            .toList();
    }
}
