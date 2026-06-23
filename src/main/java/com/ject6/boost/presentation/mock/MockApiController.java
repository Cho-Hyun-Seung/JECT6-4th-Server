package com.ject6.boost.presentation.mock;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Profile("mock")
@Tag(name = "Mock API", description = "mock 프로필에서 사용하는 인증, 사용자, 공고, 피드, 블로그 AI, 온보딩 API입니다.")
public class MockApiController {

    private static final List<Map<String, Object>> CAMPAIGNS = List.of(
            campaign(1L, "Mock cafe review campaign", "Brand One", "FOOD", "BLOG", "SEOUL", 60, 24,
                    "2026-07-05", true, "OPEN", 1420L, true),
            campaign(2L, "Mock skincare product campaign", "Brand Two", "BEAUTY", "INSTAGRAM", "BUSAN", 40, 18,
                    "2026-07-10", false, "OPEN", 950L, false),
            campaign(3L, "Mock travel stay campaign", "Brand Three", "TRAVEL", "BLOG", "JEJU", 12, 8,
                    "2026-07-15", true, "OPEN", 2310L, true),
            campaign(4L, "Mock tech gadget review", "Brand Four", "TECH_IT", "YOUTUBE", "ONLINE", 30, 15,
                    "2026-07-20", false, "OPEN", 720L, false),
            campaign(5L, "Mock living goods trial", "Brand Five", "LIVING", "BLOG", "GYEONGGI", 50, 11,
                    "2026-07-22", false, "OPEN", 640L, false),
            campaign(6L, "Mock fashion styling content", "Brand Six", "FASHION", "INSTAGRAM", "SEOUL", 35, 19,
                    "2026-07-25", true, "OPEN", 1560L, true),
            campaign(7L, "Mock pet snack review", "Brand Seven", "PET", "BLOG", "DAEGU", 25, 9,
                    "2026-07-27", false, "OPEN", 410L, false),
            campaign(8L, "Mock exhibition visit content", "Brand Eight", "CULTURE", "BLOG", "SEOUL", 20, 6,
                    "2026-07-30", false, "OPEN", 820L, false),
            campaign(9L, "Mock premium restaurant campaign", "Brand Nine", "FOOD", "BLOG", "SEOUL", 10, 7,
                    "2026-08-02", true, "OPEN", 3300L, true)
    );

    @GetMapping("/api/auth/login/{provider}")
    @Operation(summary = "[Mock] OAuth 로그인 리다이렉트", description = "실제 로그인 엔드포인트와 동일하게 /oauth2/authorization/{provider}로 이동합니다.")
    public RedirectView getLogin(@PathVariable String provider) {
        return new RedirectView("/oauth2/authorization/" + provider);
    }

    @PostMapping({"/api/auth/login/{provider}", "/api/auth/demo-login"})
    @Operation(summary = "[Mock] 로그인", description = "고정된 mock 액세스 토큰, 리프레시 토큰, 사용자 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> login(@PathVariable(required = false) String provider) {
        return ok(oauthResponse(provider == null ? "demo" : provider));
    }

    @PostMapping("/api/auth/logout")
    @Operation(summary = "[Mock] 로그아웃", description = "성공 상태와 빈 응답을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> logout() {
        return ok(null);
    }

    @PostMapping("/api/auth/refresh")
    @Operation(summary = "[Mock] 토큰 재발급", description = "고정된 mock 액세스 토큰을 재발급 응답으로 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> refresh() {
        return ok(object(
                "accessToken", "mock-access-token-refreshed",
                "tokenType", "Bearer",
                "expiresIn", 1800
        ));
    }

    @GetMapping("/api/users/me")
    @Operation(summary = "[Mock] 내 정보 조회", description = "관심 카테고리, 활동 유형, 지역, 블로그가 포함된 mock 사용자 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getMe() {
        return ok(userMe());
    }

    @PatchMapping("/api/users/me")
    @Operation(summary = "[Mock] 내 정보 수정", description = "요청 본문과 관계없이 mock 사용자 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> updateProfile(@RequestBody(required = false) Object request) {
        return ok(userMe());
    }

    @PostMapping("/api/users/me")
    @Operation(summary = "[Mock] 프로필 생성", description = "생성된 mock 사용자 프로필 응답을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> createProfile(@RequestBody(required = false) Object request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(object(
                "userId", 1L,
                "nickname", "boost-user",
                "profileCompleted", true,
                "categoryTypes", List.of("FOOD", "BEAUTY", "TRAVEL"),
                "activityTypes", List.of("BLOG", "INSTAGRAM"),
                "regionIds", List.of(1L, 2L)
        )));
    }

    @PostMapping("/api/users/me/blog")
    @Operation(summary = "[Mock] 블로그 연동", description = "생성된 mock 블로그 연동 응답을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> linkBlog(@RequestBody(required = false) Object request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(blogLink()));
    }

    @GetMapping("/api/users/nickname/check")
    @Operation(summary = "[Mock] 닉네임 중복 확인", description = "요청한 닉네임에 대해 available=true를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> checkNickname(@RequestParam String nickname) {
        return ok(object(
                "nickname", nickname,
                "available", true
        ));
    }

    @GetMapping("/api/users/nickname/random")
    @Operation(summary = "[Mock] 랜덤 닉네임 생성", description = "고정된 mock 닉네임을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> randomNickname() {
        return ok(object("nickname", "boost-maker-042"));
    }

    @DeleteMapping("/api/users/me")
    @Operation(summary = "[Mock] 회원 탈퇴", description = "성공 상태와 빈 응답을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> withdrawUser() {
        return ok(null);
    }

    @GetMapping("/campaigns")
    @Operation(summary = "[Mock] 공고 목록 조회", description = "mock 공고 목록을 간소화된 페이지 응답(content, totalElements, totalPages, size, number)으로 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getCampaigns(@PageableDefault(size = 8) Pageable pageable) {
        return ok(toPage(CAMPAIGNS, pageable));
    }

    @GetMapping("/campaigns/{id}")
    @Operation(summary = "[Mock] 공고 상세 조회", description = "요청한 공고 ID에 대한 mock 상세 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getCampaign(@PathVariable Long id) {
        return ok(campaignDetail(id));
    }

    @GetMapping("/campaigns/{id}/viewers")
    @Operation(summary = "[Mock] 공고 조회자 수 조회", description = "요청한 공고의 mock 조회자 수를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getViewers(@PathVariable Long id) {
        return ok(object("count", 128L + id));
    }

    @GetMapping("/campaigns/{id}/related")
    @Operation(summary = "[Mock] 관련 공고 조회", description = "요청한 공고를 제외한 관련 mock 공고를 최대 3개 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getRelated(@PathVariable Long id) {
        return ok(CAMPAIGNS.stream()
                .filter(campaign -> !campaign.get("id").equals(id))
                .limit(3)
                .toList());
    }

    @GetMapping("/campaigns/search")
    @Operation(summary = "[Mock] 공고 검색", description = "키워드로 mock 공고를 필터링하고 간소화된 페이지 응답(content, totalElements, totalPages, size, number)으로 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> search(
            @RequestParam String keyword,
            @PageableDefault(size = 8) Pageable pageable) {
        List<Map<String, Object>> filtered = CAMPAIGNS.stream()
                .filter(campaign -> campaign.get("title").toString().toLowerCase().contains(keyword.toLowerCase())
                        || campaign.get("brandName").toString().toLowerCase().contains(keyword.toLowerCase())
                        || campaign.get("category").toString().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
        return ok(toPage(filtered.isEmpty() ? CAMPAIGNS : filtered, pageable));
    }

    @GetMapping("/campaigns/popular")
    @Operation(summary = "[Mock] 인기 공고 조회", description = "고정된 인기 mock 공고 목록을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getPopular() {
        return ok(CAMPAIGNS.stream().limit(5).toList());
    }

    @GetMapping("/campaigns/guaranteed")
    @Operation(summary = "[Mock] 100% 당첨 공고 조회", description = "mock 데이터 중 isGuaranteed=true인 공고를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getGuaranteed() {
        return ok(CAMPAIGNS.stream()
                .filter(campaign -> Boolean.TRUE.equals(campaign.get("isGuaranteed")))
                .limit(5)
                .toList());
    }

    @GetMapping("/campaigns/closing-soon")
    @Operation(summary = "[Mock] 마감 임박 공고 조회", description = "applyEndDate 기준으로 정렬한 mock 공고를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getClosingSoon() {
        return ok(CAMPAIGNS.stream()
                .sorted((left, right) -> left.get("applyEndDate").toString().compareTo(right.get("applyEndDate").toString()))
                .limit(5)
                .toList());
    }

    @PostMapping("/campaigns/{id}/like")
    @Operation(summary = "[Mock] 공고 찜 토글", description = "liked=true와 mock 찜 수를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> toggleLike(@PathVariable Long id) {
        return ok(object(
                "liked", true,
                "likeCount", 42L + id
        ));
    }

    @PostMapping("/campaigns/{id}/apply")
    @Operation(summary = "[Mock] 공고 지원", description = "APPLIED 상태의 mock 공고 지원 응답을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> apply(@PathVariable Long id) {
        return ok(object(
                "id", 900L + id,
                "campaignId", id,
                "status", "APPLIED",
                "appliedAt", "2026-06-23T10:00:00+09:00"
        ));
    }

    @GetMapping("/campaigns/{id}/likes/analysis")
    @Operation(summary = "[Mock] 찜 분석 조회", description = "상위 카테고리와 키워드가 포함된 mock 찜 분석 결과를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getLikeAnalysis(@PathVariable Long id) {
        return ok(object(
                "campaignId", id,
                "likeCount", 80L + id,
                "analyzed", true,
                "topCategories", List.of(
                        object("category", "FOOD", "count", 32),
                        object("category", "BEAUTY", "count", 18)
                ),
                "topKeywords", List.of("review", "daily", "premium")
        ));
    }

    @GetMapping("/feed/hero")
    @Operation(summary = "[Mock] 피드 히어로 조회", description = "개인화 피드 히어로 영역의 mock 데이터를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getHero() {
        return ok(object(
                "type", "BLOG_LINKED",
                "message", "Mock personalized campaign recommendations are ready.",
                "actionLabel", "View recommendations"
        ));
    }

    @GetMapping("/feed/body")
    @Operation(summary = "[Mock] 피드 바디 조회", description = "인기, 마감 임박, 100% 당첨 공고 섹션의 mock 데이터를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getBody() {
        return ok(object(
                "popular", CAMPAIGNS.stream().limit(3).toList(),
                "closingSoon", CAMPAIGNS.stream().skip(3).limit(3).toList(),
                "guaranteed", CAMPAIGNS.stream()
                        .filter(campaign -> Boolean.TRUE.equals(campaign.get("isGuaranteed")))
                        .limit(3)
                        .toList()
        ));
    }

    @GetMapping("/feed/blogger-stories")
    @Operation(summary = "[Mock] 블로거 성공 사례 조회", description = "피드에 표시할 mock 블로거 성공 사례 카드를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getBloggerStories() {
        return ok(List.of(
                object("nickname", "mock-blogger-a", "category", "FOOD", "story", "Completed 12 campaigns."),
                object("nickname", "mock-blogger-b", "category", "TRAVEL", "story", "Selected for a premium stay.")
        ));
    }

    @PostMapping("/internal/campaigns/bulk")
    @Operation(summary = "[Mock] 공고 벌크 저장", description = "크롤러 또는 내부 동기화 테스트용 고정 저장 건수를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> bulkUpsert(@RequestBody(required = false) Object request) {
        return ok(object("saved", 3));
    }

    @PostMapping("/blog/analyze")
    @Operation(summary = "[Mock] 블로그 분석 요청", description = "HTTP 202와 함께 mock documentId, PROCESSING 상태를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> analyze(@RequestBody(required = false) Object request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(object(
                "documentId", 301L,
                "status", "PROCESSING",
                "message", "Mock analysis has been accepted.",
                "aiCreditRemaining", 2,
                "correlationId", "mock-correlation-301",
                "batchId", "mock-batch-001"
        )));
    }

    @GetMapping("/blog/analysis/{documentId}")
    @Operation(summary = "[Mock] 블로그 분석 결과 조회", description = "요청한 documentId에 대한 완료 상태의 mock 분석 결과를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getAnalysis(@PathVariable Long documentId) {
        return ok(object(
                "documentId", documentId,
                "status", "COMPLETED",
                "analysis", analysisData(),
                "analyzedAt", "2026-06-23T10:10:00+09:00"
        ));
    }

    @GetMapping("/blog/analysis/history")
    @Operation(summary = "[Mock] 블로그 분석 이력 조회", description = "잠금 상태가 포함된 mock 분석 이력 목록을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getHistory() {
        return ok(object(
                "content", List.of(
                        object("id", 301L, "channelUrl", "https://blog.naver.com/boost-user", "analyzedAt",
                                "2026-06-23T10:10:00+09:00", "isLocked", false),
                        object("id", 298L, "channelUrl", "https://blog.naver.com/boost-user", "analyzedAt",
                                "2026-06-20T09:00:00+09:00", "isLocked", false),
                        object("id", 291L, "channelUrl", "https://blog.naver.com/boost-user", "analyzedAt",
                                "2026-06-15T18:20:00+09:00", "isLocked", false)
                ),
                "totalElements", 3,
                "visibleCount", 3
        ));
    }

    @GetMapping("/blog/analysis/{analysisId}/recommendations")
    @Operation(summary = "[Mock] 추천 공고 조회", description = "요청한 analysisId에 대한 mock 추천 공고를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getRecommendations(@PathVariable Long analysisId) {
        return ok(object(
                "analysisId", analysisId,
                "campaigns", List.of(
                        object("id", 1L, "title", "Mock cafe review campaign", "fitnessScore", 94,
                                "selectionScore", 88, "reasonType", "CATEGORY_MATCH",
                                "reasonMessage", "Your blog has strong food content."),
                        object("id", 3L, "title", "Mock travel stay campaign", "fitnessScore", 87,
                                "selectionScore", 82, "reasonType", "AUDIENCE_MATCH",
                                "reasonMessage", "Your readers respond well to travel content.")
                )
        ));
    }

    @GetMapping("/blog/analysis/{analysisId}/bloggers")
    @Operation(summary = "[Mock] 유사 블로거 조회", description = "요청한 analysisId에 대한 mock 블로거 순위를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getBloggers(@PathVariable Long analysisId) {
        return ok(object(
                "category", "FOOD",
                "bloggers", List.of(
                        object("nickname", "mock-blogger-a", "overallScore", 91, "profileUrl",
                                "https://blog.naver.com/mock-a"),
                        object("nickname", "mock-blogger-b", "overallScore", 86, "profileUrl",
                                "https://blog.naver.com/mock-b")
                )
        ));
    }

    @PostMapping("/blog/chat")
    @Operation(summary = "[Mock] 블로그 AI 채팅", description = "고정된 mock AI 채팅 응답을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> chat(@RequestBody(required = false) Object request) {
        return ok(object(
                "sessionId", "mock-chat-session",
                "reply", "This is a mock AI reply.",
                "tokensUsed", 28,
                "tokensRemaining", 972
        ));
    }

    @DeleteMapping("/blog/chat/{sessionId}")
    @Operation(summary = "[Mock] 블로그 AI 채팅 세션 초기화", description = "채팅 세션 초기화 성공 상태와 빈 응답을 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> resetSession(@PathVariable String sessionId) {
        return ok(null);
    }

    @PostMapping("/onboarding/response")
    @Operation(summary = "[Mock] 온보딩 응답 저장", description = "프론트엔드 흐름 테스트를 위한 다음 온보딩 단계를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> saveStep(@RequestBody(required = false) Object request) {
        return ok(object(
                "sessionId", "mock-onboarding-session",
                "step", 1,
                "isComplete", false,
                "nextStep", 2
        ));
    }

    @GetMapping("/onboarding/recommendations")
    @Operation(summary = "[Mock] 온보딩 추천 공고 조회", description = "온보딩 세션에 대한 mock 추천 공고를 반환합니다.")
    public ResponseEntity<ApiResponse<Object>> getOnboardingRecommendations(
            @RequestParam(defaultValue = "mock-onboarding-session") String sessionId) {
        return ok(object(
                "sessionId", sessionId,
                "campaigns", CAMPAIGNS.stream()
                        .limit(4)
                        .map(campaign -> object(
                                "id", campaign.get("id"),
                                "title", campaign.get("title"),
                                "category", campaign.get("category"),
                                "thumbnailUrl", campaign.get("thumbnailUrl"),
                                "applyEndDate", campaign.get("applyEndDate")
                        ))
                        .toList()
        ));
    }

    private static Map<String, Object> oauthResponse(String provider) {
        return object(
                "access_token", "mock-access-token-" + provider,
                "refresh_token", "mock-refresh-token-" + provider,
                "expires_in", 1800,
                "token_type", "Bearer",
                "user", object(
                        "id", 1L,
                        "nickname", "boost-user",
                        "profile_image_url", "https://images.example.com/users/1.png",
                        "subscription_type", "PREMIUM",
                        "ai_credit_remaining", 99,
                        "is_profile_completed", true
                )
        );
    }

    private static Map<String, Object> userMe() {
        return object(
                "id", 1L,
                "nickname", "boost-user",
                "profileCompleted", true,
                "categoryTypes", List.of("FOOD", "BEAUTY", "TRAVEL"),
                "activityTypes", List.of("BLOG", "INSTAGRAM"),
                "regionIds", List.of(1L, 2L),
                "blogs", List.of(blogLink())
        );
    }

    private static Map<String, Object> blogLink() {
        return object(
                "id", 11L,
                "blog_url", "https://blog.naver.com/boost-user",
                "platform", "NAVER",
                "status", "ACTIVE"
        );
    }

    private static Map<String, Object> campaign(
            Long id,
            String title,
            String brandName,
            String category,
            String channel,
            String region,
            Integer recruitCount,
            Integer applyCount,
            String applyEndDate,
            Boolean isGuaranteed,
            String status,
            Long viewCount,
            boolean liked) {
        return object(
                "id", id,
                "sourcePlatform", "MOCK",
                "brandName", brandName,
                "title", title,
                "thumbnailUrl", "https://images.example.com/campaigns/" + id + ".jpg",
                "category", category,
                "type", "REVIEW",
                "channel", channel,
                "region", region,
                "parentRegionId", 1L,
                "childRegionId", 10L + id,
                "recruitCount", recruitCount,
                "applyCount", applyCount,
                "applyEndDate", applyEndDate,
                "isGuaranteed", isGuaranteed,
                "status", status,
                "viewCount", viewCount,
                "liked", liked
        );
    }

    private static Map<String, Object> campaignDetail(Long id) {
        Map<String, Object> campaign = CAMPAIGNS.stream()
                .filter(item -> item.get("id").equals(id))
                .findFirst()
                .orElse(CAMPAIGNS.get(0));

        Map<String, Object> detail = object(
                "providedContent", "Mock product or service is provided.",
                "applyStartDate", LocalDate.of(2026, 6, 23).toString(),
                "announceDate", LocalDate.of(2026, 7, 6).toString(),
                "purchaseStartDate", LocalDate.of(2026, 7, 7).toString(),
                "purchaseEndDate", LocalDate.of(2026, 7, 14).toString(),
                "reviewDeadline", LocalDate.of(2026, 7, 21).toString(),
                "mission", "Write a detailed review with at least 5 photos.",
                "searchKeywords", "mock,review,campaign",
                "sourceUrl", "https://example.com/campaigns/" + id,
                "images", List.of(object(
                        "url", "https://images.example.com/campaigns/" + id + ".jpg",
                        "altText", campaign.get("title")
                )),
                "location", object(
                        "address", "Mock address 123",
                        "lat", 37.5665,
                        "lng", 126.9780
                ),
                "campaignDetail", object(
                        "mission", "Write a detailed review with at least 5 photos.",
                        "searchKeywords", List.of("mock", "review", "campaign"),
                        "links", List.of("https://example.com/campaigns/" + id),
                        "caution", "This is mock data.",
                        "additionalNotice", "Use only for frontend development."
                )
        );

        detail.putAll(campaign);
        return detail;
    }

    private static Map<String, Object> analysisData() {
        return object(
                "summary", "Mock blog has strong review-style content.",
                "keyTopics", List.of("food", "daily", "review"),
                "tone", "FRIENDLY",
                "targetAudience", "Lifestyle readers",
                "suggestions", List.of("Add more comparison sections.", "Use clearer CTA text."),
                "overallScore", 87,
                "percentile", 92,
                "blogType", "REVIEWER",
                "strengthSummary", "Clear photos and practical descriptions.",
                "weaknessSummary", "Posting frequency can be improved.",
                "topCategories", List.of(
                        object("category", "FOOD", "score", 94),
                        object("category", "BEAUTY", "score", 81)
                ),
                "metrics", List.of(
                        object("name", "contentQuality", "score", 88),
                        object("name", "audienceFit", "score", 84)
                )
        );
    }

    private static ResponseEntity<ApiResponse<Object>> ok(Object data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    private static Map<String, Object> toPage(List<Map<String, Object>> source, Pageable pageable) {
        int size = Math.max(pageable.getPageSize(), 1);
        int number = Math.max(pageable.getPageNumber(), 0);
        int start = Math.min(number * size, source.size());
        int end = Math.min(start + size, source.size());
        int totalPages = (int) Math.ceil((double) source.size() / size);

        return object(
                "content", source.subList(start, end),
                "totalElements", source.size(),
                "totalPages", totalPages,
                "size", size,
                "number", number
        );
    }

    private static Map<String, Object> object(Object... entries) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put((String) entries[i], entries[i + 1]);
        }
        return map;
    }
}
