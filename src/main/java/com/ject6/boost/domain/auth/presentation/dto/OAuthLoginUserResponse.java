package com.ject6.boost.domain.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ject6.boost.domain.user.domain.constant.SubscriptionType;
import com.ject6.boost.domain.user.domain.entity.User;

public record OAuthLoginUserResponse(
        Long id,
        String nickname,
        @JsonProperty("profile_image_url")
        String profileImageUrl,
        @JsonProperty("subscription_type")
        SubscriptionType subscriptionType,
        @JsonProperty("ai_credit_remaining")
        int aiCreditRemaining,
        @JsonProperty("is_profile_completed")
        boolean profileCompleted
) {

    public static OAuthLoginUserResponse from(User user) {
        return new OAuthLoginUserResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getSubscriptionTypeOrDefault(),
                user.getAiCreditRemainingOrDefault(),
                user.isProfileCompleted()
        );
    }
}
