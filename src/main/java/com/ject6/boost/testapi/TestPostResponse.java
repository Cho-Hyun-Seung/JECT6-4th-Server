package com.ject6.boost.testapi;

import java.time.LocalDateTime;

public record TestPostResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt
) {

    public static TestPostResponse from(TestPost testPost) {
        return new TestPostResponse(
                testPost.getId(),
                testPost.getTitle(),
                testPost.getContent(),
                testPost.getCreatedAt()
        );
    }
}
