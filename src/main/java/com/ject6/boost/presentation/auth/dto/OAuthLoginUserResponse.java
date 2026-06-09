package com.ject6.boost.presentation.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ject6.boost.domain.user.constant.SubscriptionType;
import com.ject6.boost.domain.user.entity.User;

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
