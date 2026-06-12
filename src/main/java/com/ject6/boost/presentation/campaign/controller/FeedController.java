package com.ject6.boost.presentation.campaign.controller;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.application.campaign.service.CampaignService;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.repository.UserBlogRepository;
import com.ject6.boost.domain.user.repository.UserRepository;
import com.ject6.boost.presentation.campaign.controller.docs.FeedApi;
import com.ject6.boost.presentation.campaign.dto.CampaignListResponse;
import com.ject6.boost.presentation.campaign.dto.FeedBodyResponse;
import com.ject6.boost.presentation.campaign.dto.HeroResponse;
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
    private final UserRepository userRepository;
    private final UserBlogRepository userBlogRepository;

    @GetMapping("/hero")
    public ResponseEntity<ApiResponse<HeroResponse>> getHero(
        @AuthenticationPrincipal AuthenticatedUser principal) {

        HeroResponse hero;

        if (principal == null) {
            hero = HeroResponse.forAnonymous();
        } else {
            User user = userRepository.findActiveById(principal.userId()).orElse(null);
            boolean blogLinked = user != null && !userBlogRepository.findActiveByUser(user).isEmpty();
            hero = blogLinked ? HeroResponse.forBlogLinked() : HeroResponse.forLoggedIn();
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
