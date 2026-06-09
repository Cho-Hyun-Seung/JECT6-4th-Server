package com.ject6.boost.domain.campaign.presentation.dto;

import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CampaignDetailResponse {

    private Long id;
    private String sourcePlatform;
    private String brandName;
    private String title;
    private String thumbnailUrl;
    private String category;
    private String type;
    private String channel;
    private String region;
    private String providedContent;
    private Integer recruitCount;
    private Integer applyCount;
    private LocalDate applyStartDate;
    private LocalDate applyEndDate;
    private LocalDate announceDate;
    private LocalDate purchaseStartDate;
    private LocalDate purchaseEndDate;
    private LocalDate reviewDeadline;
    private String mission;
    private String searchKeywords;
    private Boolean isGuaranteed;
    private String status;
    private String sourceUrl;
    private Long viewCount;

    public static CampaignDetailResponse from(Campaign campaign) {
        return CampaignDetailResponse.builder()
            .id(campaign.getId())
            .sourcePlatform(campaign.getSourcePlatform())
            .brandName(campaign.getBrandName())
            .title(campaign.getTitle())
            .thumbnailUrl(campaign.getThumbnailUrl())
            .category(campaign.getCategory() != null
                ? campaign.getCategory().name() : null)
            .type(campaign.getType() != null
                ? campaign.getType().name() : null)
            .channel(campaign.getChannel())
            .region(campaign.getRegion())
            .providedContent(campaign.getProvidedContent())
            .recruitCount(campaign.getRecruitCount())
            .applyCount(campaign.getApplyCount())
            .applyStartDate(campaign.getApplyStartDate())
            .applyEndDate(campaign.getApplyEndDate())
            .announceDate(campaign.getAnnounceDate())
            .purchaseStartDate(campaign.getPurchaseStartDate())
            .purchaseEndDate(campaign.getPurchaseEndDate())
            .reviewDeadline(campaign.getReviewDeadline())
            .mission(campaign.getMission())
            .searchKeywords(campaign.getSearchKeywords())
            .isGuaranteed(campaign.getIsGuaranteed())
            .status(campaign.getStatus() != null
                ? campaign.getStatus().name() : null)
            .sourceUrl(campaign.getSourceUrl())
            .viewCount(campaign.getViewCount())
            .build();
    }
}