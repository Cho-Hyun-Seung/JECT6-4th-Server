package com.ject6.boost.presentation.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ProfileRequest(
        @Schema(description = "닉네임")
        String nickname,
        @Schema(
                description = "관심 카테고리. 허용값: FOOD, BEAUTY, CULTURE, TRAVEL, TECH_IT, PET, LIVING, FASHION, ETC",
                allowableValues = {"FOOD", "BEAUTY", "CULTURE", "TRAVEL", "TECH_IT", "PET", "LIVING", "FASHION", "ETC"}
        )
        List<String> categoryTypes
) {
}
