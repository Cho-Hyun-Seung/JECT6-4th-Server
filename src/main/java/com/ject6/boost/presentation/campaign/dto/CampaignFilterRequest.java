package com.ject6.boost.presentation.campaign.dto;

import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignFilterRequest {
    @Schema(
            description = "카테고리 다중 필터. 허용값: FOOD, BEAUTY, FASHION, LIVING, PET, TECH_IT, TRAVEL, CULTURE, ETC",
            allowableValues = {"FOOD", "BEAUTY", "FASHION", "LIVING", "PET", "TECH_IT", "TRAVEL", "CULTURE", "ETC"}
    )
    private List<CampaignCategory> categories;

    @Schema(
            description = "카테고리 단일 필터. categories가 있으면 categories가 우선 적용됩니다. 허용값: FOOD, BEAUTY, FASHION, LIVING, PET, TECH_IT, TRAVEL, CULTURE, ETC",
            allowableValues = {"FOOD", "BEAUTY", "FASHION", "LIVING", "PET", "TECH_IT", "TRAVEL", "CULTURE", "ETC"}
    )
    private CampaignCategory category;

    @Schema(description = "상위 지역 ID")
    private Long parentRegionId;

    @Schema(description = "하위 지역 ID")
    private Long childRegionId;

    @Schema(description = "지역명")
    private String region;

    public List<CampaignCategory> getCategories() {
        if (categories != null && !categories.isEmpty()) {
            return categories;
        }
        if (category != null) {
            return List.of(category);
        }
        return null;
    }
}
