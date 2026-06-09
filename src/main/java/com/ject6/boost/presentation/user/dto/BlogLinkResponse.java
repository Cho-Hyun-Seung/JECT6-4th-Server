package com.ject6.boost.presentation.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ject6.boost.domain.user.constant.BlogPlatform;
import com.ject6.boost.domain.user.constant.BlogStatus;
import com.ject6.boost.domain.user.entity.UserBlog;

public record BlogLinkResponse(
        Long id,
        @JsonProperty("blog_url")
        String blogUrl,
        BlogPlatform platform,
        BlogStatus status
) {

    public static BlogLinkResponse from(UserBlog blog) {
        return new BlogLinkResponse(
                blog.getId(),
                blog.getBlogUrl(),
                blog.getPlatform(),
                blog.getStatus()
        );
    }
}
