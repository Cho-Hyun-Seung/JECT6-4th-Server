package com.ject6.boost.domain.user.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UserProfileUpdateRequest(
        String nickname,
        @JsonProperty("interest_categories")
        List<String> interestCategories,
        List<String> channels,
        List<Long> regions
) {
}
