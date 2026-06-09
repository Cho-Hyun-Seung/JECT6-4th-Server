package com.ject6.boost.presentation.campaign.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HeroResponse {

    private String type;        // ANONYMOUS / LOGGED_IN / BLOG_LINKED
    private String message;     // 배너 메시지
    private String actionLabel; // 버튼 텍스트

    public static HeroResponse forAnonymous() {
        return HeroResponse.builder()
            .type("ANONYMOUS")
            .message("나에게 딱 맞는 체험단을 찾아보세요")
            .actionLabel("지금 시작하기")
            .build();
    }

    public static HeroResponse forLoggedIn() {
        return HeroResponse.builder()
            .type("LOGGED_IN")
            .message("블로그를 연동하고 AI 맞춤 추천을 받아보세요")
            .actionLabel("블로그 연동하기")
            .build();
    }

    public static HeroResponse forBlogLinked() {
        return HeroResponse.builder()
            .type("BLOG_LINKED")
            .message("AI가 분석한 나만의 맞춤 체험단")
            .actionLabel("추천 공고 보기")
            .build();
    }
}
