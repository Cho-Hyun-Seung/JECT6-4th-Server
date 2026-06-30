package com.ject6.boost.presentation.campaign.dto;

import com.ject6.boost.domain.campaign.entity.Influencer;

public record BloggerStoryResponse(
        Long id,
        String nickname,
        String blogName,
        String category,
        String title,
        String thumbnailUrl,
        String blogUrl
) {

    public static BloggerStoryResponse from(Influencer influencer) {
        String title = influencer.getTitle();
        return new BloggerStoryResponse(
                influencer.getId(),
                influencer.getInfluencerName(),
                influencer.getBlogName(),
                influencer.getCategory() != null ? influencer.getCategory().name() : null,
                title,
                influencer.getThumbnailUrl(),
                influencer.getBlogUrl()
        );
    }
}
