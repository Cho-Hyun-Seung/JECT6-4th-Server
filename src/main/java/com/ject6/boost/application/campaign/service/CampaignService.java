package com.ject6.boost.application.campaign.service;

import com.ject6.boost.application.campaign.exception.CampaignErrorCode;
import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.domain.campaign.entity.Campaign;
import com.ject6.boost.domain.campaign.repository.CampaignRepository;
import com.ject6.boost.domain.campaign.repository.UserCampaignLikeRepository;
import com.ject6.boost.infrastructure.common.redis.ViewerCountService;
import com.ject6.boost.presentation.campaign.dto.CampaignBulkRequest;
import com.ject6.boost.presentation.campaign.dto.CampaignDetailResponse;
import com.ject6.boost.presentation.campaign.dto.CampaignFilterRequest;
import com.ject6.boost.presentation.campaign.dto.CampaignListResponse;
import java.util.List;
import java.util.Set;
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
    private final UserCampaignLikeRepository userCampaignLikeRepository;
    private final ViewerCountService viewerCountService;

    public Page<CampaignListResponse> getCampaigns(
            CampaignFilterRequest filter, Pageable pageable) {
        return getCampaigns(filter, pageable, null);
    }

    public Page<CampaignListResponse> getCampaigns(
            CampaignFilterRequest filter, Pageable pageable, Long userId) {
        Page<Campaign> campaigns = campaignRepository.search(filter, pageable);
        Set<Long> likedCampaignIds = findLikedCampaignIds(userId, campaigns.getContent());

        return campaigns.map(campaign ->
                CampaignListResponse.from(campaign, likedCampaignIds.contains(campaign.getId())));
    }

    public CampaignDetailResponse getCampaign(Long id) {
        return getCampaign(id, null);
    }

    public CampaignDetailResponse getCampaign(Long id, Long userId) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CampaignErrorCode.CAMPAIGN_NOT_FOUND));
        return CampaignDetailResponse.from(campaign, isLiked(userId, id));
    }

    public Long getViewerCount(Long id) {
        return viewerCountService.getCount(id);
    }

    public List<CampaignListResponse> getRelated(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CampaignErrorCode.CAMPAIGN_NOT_FOUND));
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

    @Transactional
    public int bulkUpsert(List<CampaignBulkRequest.Item> items) {
        return campaignRepository.upsertBulk(items);
    }

    private Set<Long> findLikedCampaignIds(Long userId, List<Campaign> campaigns) {
        if (userId == null || campaigns.isEmpty()) {
            return Set.of();
        }
        List<Long> campaignIds = campaigns.stream()
                .map(Campaign::getId)
                .toList();
        return Set.copyOf(userCampaignLikeRepository.findCampaignIdsByUserIdAndCampaignIdIn(
                userId, campaignIds));
    }

    private boolean isLiked(Long userId, Long campaignId) {
        if (userId == null) {
            return false;
        }
        return userCampaignLikeRepository.existsByUserIdAndCampaignId(userId, campaignId);
    }
}
