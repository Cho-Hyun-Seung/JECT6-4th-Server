package com.ject6.boost.presentation.campaign.controller.docs;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.campaign.dto.FeedBodyResponse;
import com.ject6.boost.presentation.campaign.dto.HeroResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Feed", description = "홈 피드 API")
public interface FeedApi {

    @Operation(summary = "히어로 배너", description = "유저 상태별 히어로 배너 반환 (비로그인/로그인/블로그연동)")
    ResponseEntity<ApiResponse<HeroResponse>> getHero();

    @Operation(summary = "메인 바디", description = "인기/마감임박/100%당첨 섹션 데이터")
    ResponseEntity<ApiResponse<FeedBodyResponse>> getBody();

    @Operation(summary = "블로거 성공 사례", description = "유명 블로거 성공 사례 (추후 구현)")
    ResponseEntity<ApiResponse<Object>> getBloggerStories();
}
