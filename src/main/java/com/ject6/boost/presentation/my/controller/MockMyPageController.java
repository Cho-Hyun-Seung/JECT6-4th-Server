package com.ject6.boost.presentation.my.controller;

import com.ject6.boost.domain.user.constant.CategoryType;
import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.my.dto.PointBalanceResponse;
import com.ject6.boost.presentation.my.dto.PointWithdrawResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my")
@Profile("mock")
@Tag(name = "Mock 마이페이지", description = "mock 프로필에서 사용하는 마이페이지 API입니다.")
public class MockMyPageController {

    private static final List<Map<String, Object>> RECENT_VIEW_CAMPAIGNS = List.of(
            campaign(100L, "New product review campaign", "Brand A", "FOOD", "2026-07-01"),
            campaign(101L, "Summer skincare review campaign", "Brand B", "BEAUTY", "2026-07-04"),
            campaign(102L, "Jeju stay blog review", "Brand C", "TRAVEL", "2026-07-08"),
            campaign(103L, "Wireless keyboard product review", "Brand D", "TECH_IT", "2026-07-12"),
            campaign(104L, "Home cafe coffee trial", "Brand E", "FOOD", "2026-07-15"),
            campaign(105L, "Daily fashion item content", "Brand F", "FASHION", "2026-07-18"),
            campaign(106L, "Pet snack review campaign", "Brand G", "PET", "2026-07-21"),
            campaign(107L, "Eco living goods campaign", "Brand H", "LIVING", "2026-07-24"),
            campaign(108L, "Exhibition visit content", "Brand I", "CULTURE", "2026-07-27"),
            campaign(109L, "Premium restaurant review", "Brand J", "FOOD", "2026-07-30"),
            campaign(110L, "Travel goods trial", "Brand K", "TRAVEL", "2026-08-01"),
            campaign(111L, "Beauty device review", "Brand L", "BEAUTY", "2026-08-03")
    );

    private static final List<Map<String, Object>> LIKED_CAMPAIGNS = List.of(
            RECENT_VIEW_CAMPAIGNS.get(0),
            RECENT_VIEW_CAMPAIGNS.get(1),
            RECENT_VIEW_CAMPAIGNS.get(2),
            RECENT_VIEW_CAMPAIGNS.get(4),
            RECENT_VIEW_CAMPAIGNS.get(6)
    );

    private static final List<Map<String, Object>> RECENT_APPLIED_CAMPAIGNS = List.of(
            appliedCampaign(10L, 100L, "New product review campaign", "Brand A",
                    "APPLIED", "2026-06-23", "2026-07-01"),
            appliedCampaign(11L, 101L, "Summer skincare review campaign", "Brand B",
                    "REVIEWING", "2026-06-20", "2026-07-04"),
            appliedCampaign(12L, 102L, "Jeju stay blog review", "Brand C",
                    "SELECTED", "2026-06-18", "2026-07-08")
    );

    private static final List<Map<String, Object>> AI_HISTORY = List.of(
            object("historyId", 31L, "diagnosisDate", "20260623", "documentId", 201L),
            object("historyId", 28L, "diagnosisDate", "20260620", "documentId", 198L),
            object("historyId", 21L, "diagnosisDate", "20260615", "documentId", 185L),
            object("historyId", 18L, "diagnosisDate", "20260610", "documentId", 172L)
    );

    @GetMapping({"", "/account"})
    @Operation(
            summary = "[Mock] 내 계정 조회",
            description = "마이페이지 계정 영역에 필요한 nickname, blogUrl, interestCategories를 반환합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getMy() {
        return ok(object(
                "nickname", "boost-user",
                "blogUrl", "https://blog.naver.com/example",
                "interestCategories", List.of(CategoryType.FOOD, CategoryType.BEAUTY, CategoryType.TRAVEL)
        ));
    }

    @GetMapping({"/campaigns", "/campaigns/summary"})
    @Operation(
            summary = "[Mock] 내 관심 공고 요약 조회",
            description = "내 관심 공고 최상위 페이지에 필요한 recentViewCount, likedCount, recentAppliedCampaign을 반환합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getMyCampaigns() {
        return ok(object(
                "recentViewCount", RECENT_VIEW_CAMPAIGNS.size(),
                "likedCount", LIKED_CAMPAIGNS.size(),
                "recentAppliedCampaign", RECENT_APPLIED_CAMPAIGNS.stream()
                        .map(campaign -> object(
                                "id", campaign.get("id"),
                                "campaignId", campaign.get("campaignId"),
                                "title", campaign.get("campaignTitle"),
                                "brandName", campaign.get("brandName"),
                                "status", campaign.get("status"),
                                "appliedAt", campaign.get("appliedAt"),
                                "applyEndDate", campaign.get("applyEndDate")
                        ))
                        .toList()
        ));
    }

    @GetMapping("/campaigns/recent-views")
    @Operation(
            summary = "[Mock] 최근 조회 공고 목록 조회",
            description = "content, totalElements, totalPages, size, number만 포함한 간소화된 페이지 응답을 반환합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getRecentViews(
            @PageableDefault(size = 8) Pageable pageable) {
        return ok(page(RECENT_VIEW_CAMPAIGNS, pageable));
    }

    @GetMapping("/campaigns/likes")
    @Operation(
            summary = "[Mock] 좋아요한 공고 목록 조회",
            description = "최근 조회 공고와 동일한 간소화된 페이지 응답으로 좋아요한 공고 카드를 반환합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getLikes(
            @PageableDefault(size = 8) Pageable pageable) {
        return ok(page(LIKED_CAMPAIGNS, pageable));
    }

    @GetMapping("/campaigns/recent-applies")
    @Operation(
            summary = "[Mock] 최근 지원한 공고 목록 조회",
            description = "id, campaignId, campaignTitle, brandName, appliedAt, applyEndDate를 포함한 지원 공고 카드를 반환합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getRecentApplies(
            @PageableDefault(size = 8) Pageable pageable) {
        return ok(page(RECENT_APPLIED_CAMPAIGNS.stream()
                .map(campaign -> object(
                        "id", campaign.get("id"),
                        "campaignId", campaign.get("campaignId"),
                        "campaignTitle", campaign.get("campaignTitle"),
                        "brandName", campaign.get("brandName"),
                        "appliedAt", campaign.get("appliedAt"),
                        "applyEndDate", campaign.get("applyEndDate")
                ))
                .toList(), pageable));
    }

    @GetMapping("/ai-history")
    @Operation(
            summary = "[Mock] AI 히스토리 조회",
            description = "aiHistory 목록을 반환합니다. 히스토리를 열 때 historyId를 사용하며 diagnosisDate는 YYYYMMDD 형식입니다."
    )
    public ResponseEntity<ApiResponse<Object>> getAiHistory(
            @RequestParam(defaultValue = "3") int size) {
        int toIndex = Math.min(Math.max(size, 0), AI_HISTORY.size());
        return ok(object("aiHistory", AI_HISTORY.subList(0, toIndex)));
    }

    @GetMapping("/campaigns/{id}")
    @Operation(
            summary = "[Mock] 내 공고 상세 조회",
            description = "기존 마이페이지 공고 상세 화면을 위한 mock 응답을 반환합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getMyCampaignDetail(@PathVariable Long id) {
        return ok(RECENT_APPLIED_CAMPAIGNS.stream()
                .filter(campaign -> campaign.get("id").equals(id))
                .findFirst()
                .orElse(RECENT_APPLIED_CAMPAIGNS.get(0)));
    }

    @GetMapping("/recent-views")
    @Operation(
            summary = "[Mock] 최근 본 공고 조회",
            description = "페이지네이션 없이 최근 본 공고 목록을 반환하는 기존 mock 엔드포인트입니다."
    )
    public ResponseEntity<ApiResponse<Object>> getLegacyRecentViews() {
        return ok(RECENT_VIEW_CAMPAIGNS);
    }

    @GetMapping("/likes")
    @Operation(
            summary = "[Mock] 찜한 공고 조회",
            description = "페이지네이션 없이 찜한 공고 목록을 반환하는 기존 mock 엔드포인트입니다."
    )
    public ResponseEntity<ApiResponse<Object>> getLegacyLikes() {
        return ok(LIKED_CAMPAIGNS);
    }

    @GetMapping("/points")
    @Operation(
            summary = "[Mock] 포인트 잔액 조회",
            description = "mock 포인트 잔액과 거래 내역을 반환합니다."
    )
    public ResponseEntity<ApiResponse<PointBalanceResponse>> getPoints() {
        return ResponseEntity.ok(ApiResponse.success(new PointBalanceResponse(
                128000,
                List.of(
                        new PointBalanceResponse.TransactionItem(
                                401L,
                                "EARN",
                                30000,
                                "Campaign reward",
                                128000,
                                OffsetDateTime.parse("2026-06-23T09:00:00+09:00")
                        ),
                        new PointBalanceResponse.TransactionItem(
                                402L,
                                "WITHDRAW",
                                -50000,
                                "Withdraw request",
                                98000,
                                OffsetDateTime.parse("2026-06-15T12:00:00+09:00")
                        )
                )
        )));
    }

    @PostMapping("/points/withdraw")
    @Operation(
            summary = "[Mock] 포인트 출금 신청",
            description = "요청 본문 검증 없이 PENDING 상태의 고정 출금 응답을 반환합니다."
    )
    public ResponseEntity<ApiResponse<PointWithdrawResponse>> withdraw() {
        return ResponseEntity.ok(ApiResponse.success(new PointWithdrawResponse(
                501L,
                5000,
                "PENDING",
                "Mock Bank",
                "****1234",
                OffsetDateTime.parse("2026-06-23T10:30:00+09:00")
        )));
    }

    private static Map<String, Object> campaign(
            Long id,
            String title,
            String brandName,
            String category,
            String applyEndDate) {
        return object(
                "id", id,
                "title", title,
                "brandName", brandName,
                "category", category,
                "thumbnailUrl", "https://images.example.com/campaigns/" + id + ".jpg",
                "applyEndDate", applyEndDate
        );
    }

    private static Map<String, Object> appliedCampaign(
            Long id,
            Long campaignId,
            String campaignTitle,
            String brandName,
            String status,
            String appliedAt,
            String applyEndDate) {
        return object(
                "id", id,
                "campaignId", campaignId,
                "campaignTitle", campaignTitle,
                "brandName", brandName,
                "status", status,
                "appliedAt", appliedAt,
                "applyEndDate", applyEndDate
        );
    }

    private static Map<String, Object> page(List<Map<String, Object>> source, Pageable pageable) {
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

    private static ResponseEntity<ApiResponse<Object>> ok(Object data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    private static Map<String, Object> object(Object... entries) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put((String) entries[i], entries[i + 1]);
        }
        return map;
    }
}
