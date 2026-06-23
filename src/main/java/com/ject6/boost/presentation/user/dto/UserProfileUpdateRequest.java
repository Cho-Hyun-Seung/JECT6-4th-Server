package com.ject6.boost.presentation.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UserProfileUpdateRequest(
        @Schema(description = "변경할 닉네임")
        String nickname,
        @JsonProperty("interest_categories")
        @Schema(
                description = "관심 카테고리. 허용값: FOOD, BEAUTY, CULTURE, TRAVEL, TECH_IT, PET, LIVING, FASHION, ETC",
                allowableValues = {"FOOD", "BEAUTY", "CULTURE", "TRAVEL", "TECH_IT", "PET", "LIVING", "FASHION", "ETC"}
        )
        List<String> interestCategories
) {
}
