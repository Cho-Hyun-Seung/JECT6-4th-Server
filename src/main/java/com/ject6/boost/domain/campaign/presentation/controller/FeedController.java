package com.ject6.boost.domain.campaign.presentation.controller;

import com.ject6.boost.common.dto.ApiResponse;
import com.ject6.boost.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.domain.campaign.application.service.CampaignService;
import com.ject6.boost.domain.campaign.presentation.controller.docs.FeedApi;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignListResponse;
import com.ject6.boost.domain.campaign.presentation.dto.FeedBodyResponse;
import com.ject6.boost.domain.campaign.presentation.dto.HeroResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final CampaignService campaignService;

    @GetMapping("/hero")
    public ResponseEntity<ApiResponse<HeroResponse>> getHero(
        @AuthenticationPrincipal AuthenticatedUser user) {

        HeroResponse hero;

        if (user == null) {
            // 비로그인
            hero = HeroResponse.forAnonymous();
        } else {
            // 로그인 (블로그 연동 여부는 BC1이 AuthenticatedUser에 필드 추가 후 분기 예정)
            hero = HeroResponse.forLoggedIn();
        }

        return ResponseEntity.ok(ApiResponse.success(hero));
    }

    @GetMapping("/body")
    public ResponseEntity<ApiResponse<FeedBodyResponse>> getBody() {
        List<CampaignListResponse> popular    = campaignService.getPopular();
        List<CampaignListResponse> closingSoon = campaignService.getClosingSoon();
        List<CampaignListResponse> guaranteed  = campaignService.getGuaranteed();

        return ResponseEntity.ok(ApiResponse.success(
            FeedBodyResponse.of(popular, closingSoon, guaranteed)));
    }

    @GetMapping("/blogger-stories")
    public ResponseEntity<ApiResponse<Object>> getBloggerStories() {
        // BC3 AI 연동 후 구현 예정
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }

}
