package com.ject6.boost.domain.campaign.presentation.dto;

import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CampaignListResponse {

    private Long id;
    private String sourcePlatform;
    private String brandName;
    private String title;
    private String thumbnailUrl;
    private String category;
    private String type;
    private String channel;
    private String region;
    private Integer recruitCount;
    private Integer applyCount;
    private LocalDate applyEndDate;
    private Boolean isGuaranteed;
    private String status;
    private Long viewCount;

    public static CampaignListResponse from(Campaign campaign) {
        return CampaignListResponse.builder()
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
            .recruitCount(campaign.getRecruitCount())
            .applyCount(campaign.getApplyCount())
            .applyEndDate(campaign.getApplyEndDate())
            .isGuaranteed(campaign.getIsGuaranteed())
            .status(campaign.getStatus() != null
                ? campaign.getStatus().name() : null)
            .viewCount(campaign.getViewCount())
            .build();
    }
}
