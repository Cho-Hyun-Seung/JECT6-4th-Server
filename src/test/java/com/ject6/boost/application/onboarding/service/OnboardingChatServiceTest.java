package com.ject6.boost.application.onboarding.service;

import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.constant.CampaignType;
import com.ject6.boost.domain.campaign.entity.Campaign;
import com.ject6.boost.domain.campaign.repository.CampaignRepository;
import com.ject6.boost.application.onboarding.exception.OnboardingErrorCode;
import com.ject6.boost.domain.onboarding.entity.OnboardingResponse;
import com.ject6.boost.domain.onboarding.repository.OnboardingResponseRepository;
import com.ject6.boost.presentation.onboarding.dto.OnboardingRecommendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OnboardingChatServiceTest {

    @Mock
    private OnboardingResponseRepository onboardingResponseRepository;
    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private OnboardingChatService onboardingChatService;

    private static final String SESSION_ID = "test-session-001";

    @Test
    @DisplayName("세션이 없으면 SESSION_NOT_FOUND 예외를 던진다")
    void getRecommendations_sessionNotFound() {
        given(onboardingResponseRepository.findBySessionId(SESSION_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> onboardingChatService.getRecommendations(SESSION_ID))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(OnboardingErrorCode.SESSION_NOT_FOUND));
    }

    @Test
    @DisplayName("4단계 미완료 세션이면 ONBOARDING_NOT_COMPLETE 예외를 던진다")
    void getRecommendations_notComplete() {
        OnboardingResponse incomplete = OnboardingResponse.create(SESSION_ID);
        incomplete.applyStep(1, "BEAUTY");
        given(onboardingResponseRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(incomplete));

        assertThatThrownBy(() -> onboardingChatService.getRecommendations(SESSION_ID))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(OnboardingErrorCode.ONBOARDING_NOT_COMPLETE));
    }

    @Test
    @DisplayName("카테고리+유형 매칭 결과가 있으면 최대 8개를 반환한다")
    void getRecommendations_returnsMatchedCampaigns() {
        OnboardingResponse complete = completeSession("BEAUTY", "YES", "DELIVERY", "MIDDLE");
        given(onboardingResponseRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(complete));

        List<Campaign> matched = buildCampaigns(5, "BEAUTY", "DELIVERY");
        given(campaignRepository.findActiveByCategoryAndType(CampaignCategory.BEAUTY, CampaignType.DELIVERY)).willReturn(matched);
        given(campaignRepository.findActiveByCategory(CampaignCategory.BEAUTY)).willReturn(List.of());

        OnboardingRecommendResponse result = onboardingChatService.getRecommendations(SESSION_ID);

        assertThat(result.campaigns()).hasSize(5);
        assertThat(result.sessionId()).isEqualTo(SESSION_ID);
    }

    @Test
    @DisplayName("1순위 결과가 부족하면 카테고리 전용으로 fallback 보충한다")
    void getRecommendations_fillsFromCategoryFallback() {
        OnboardingResponse complete = completeSession("FOOD", "NO", "VISIT", "BEGINNER");
        given(onboardingResponseRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(complete));

        List<Campaign> p1Results = buildCampaigns(3, "FOOD", "VISIT");
        List<Campaign> categoryResults = new ArrayList<>();
        for (int i = 4; i <= 13; i++) {
            categoryResults.add(buildCampaign((long) i, "FOOD", "DELIVERY", null, null, null));
        }
        given(campaignRepository.findActiveByCategoryAndType(CampaignCategory.FOOD, CampaignType.VISIT)).willReturn(p1Results);
        given(campaignRepository.findActiveByCategory(CampaignCategory.FOOD)).willReturn(categoryResults);

        OnboardingRecommendResponse result = onboardingChatService.getRecommendations(SESSION_ID);

        assertThat(result.campaigns()).hasSize(8);
    }

    @Test
    @DisplayName("카테고리 결과도 부족하면 전체 fallback으로 최대 8개를 채운다")
    void getRecommendations_fillsFromGlobalFallback() {
        OnboardingResponse complete = completeSession("TRAVEL", "PLAN", "ANY", "ACTIVE");
        given(onboardingResponseRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(complete));

        List<Campaign> categoryResults = buildCampaigns(2, "TRAVEL", null);
        List<Campaign> fallback = new ArrayList<>();
        for (int i = 3; i <= 17; i++) {
            fallback.add(buildCampaign((long) i, "FOOD", null, null, null, null));
        }
        given(campaignRepository.findActiveByCategory(CampaignCategory.TRAVEL)).willReturn(categoryResults);
        given(campaignRepository.findActiveFallback(20)).willReturn(fallback);

        OnboardingRecommendResponse result = onboardingChatService.getRecommendations(SESSION_ID);

        assertThat(result.campaigns()).hasSize(8);
    }

    @Test
    @DisplayName("추천 가능한 캠페인이 없으면 빈 목록을 정상 반환한다")
    void getRecommendations_returnsEmptyWhenNoCampaigns() {
        OnboardingResponse complete = completeSession("PET", "YES", "ANY", "MIDDLE");
        given(onboardingResponseRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(complete));
        given(campaignRepository.findActiveByCategory(CampaignCategory.PET)).willReturn(List.of());
        given(campaignRepository.findActiveFallback(20)).willReturn(List.of());

        OnboardingRecommendResponse result = onboardingChatService.getRecommendations(SESSION_ID);

        assertThat(result.campaigns()).isEmpty();
    }

    @Test
    @DisplayName("BEGINNER 활동 수준이면 보장형 캠페인이 앞에 정렬된다")
    void getRecommendations_beginnerSortsGuaranteedFirst() {
        OnboardingResponse complete = completeSession("BEAUTY", "YES", "ANY", "BEGINNER");
        given(onboardingResponseRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(complete));

        Campaign notGuaranteed = buildCampaign(1L, "BEAUTY", null, false, null, null);
        Campaign guaranteed = buildCampaign(2L, "BEAUTY", null, true, null, null);
        given(campaignRepository.findActiveByCategory(CampaignCategory.BEAUTY)).willReturn(List.of(notGuaranteed, guaranteed));
        given(campaignRepository.findActiveFallback(20)).willReturn(List.of());

        OnboardingRecommendResponse result = onboardingChatService.getRecommendations(SESSION_ID);

        assertThat(result.campaigns().get(0).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("ACTIVE 활동 수준이면 보상 금액이 높은 캠페인이 앞에 정렬된다")
    void getRecommendations_activeSortsByRewardFirst() {
        OnboardingResponse complete = completeSession("FASHION", "YES", "ANY", "ACTIVE");
        given(onboardingResponseRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(complete));

        Campaign lowReward = buildCampaign(1L, "FASHION", null, null, 1000, null);
        Campaign highReward = buildCampaign(2L, "FASHION", null, null, 50000, null);
        given(campaignRepository.findActiveByCategory(CampaignCategory.FASHION)).willReturn(List.of(lowReward, highReward));
        given(campaignRepository.findActiveFallback(20)).willReturn(List.of());

        OnboardingRecommendResponse result = onboardingChatService.getRecommendations(SESSION_ID);

        assertThat(result.campaigns().get(0).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("중복 캠페인이 결과에 포함되지 않는다")
    void getRecommendations_noDuplicates() {
        OnboardingResponse complete = completeSession("FOOD", "YES", "DELIVERY", "MIDDLE");
        given(onboardingResponseRepository.findBySessionId(SESSION_ID)).willReturn(Optional.of(complete));

        List<Campaign> sharedCampaigns = buildCampaigns(5, "FOOD", "DELIVERY");
        given(campaignRepository.findActiveByCategoryAndType(CampaignCategory.FOOD, CampaignType.DELIVERY)).willReturn(sharedCampaigns);
        given(campaignRepository.findActiveByCategory(CampaignCategory.FOOD)).willReturn(sharedCampaigns);

        OnboardingRecommendResponse result = onboardingChatService.getRecommendations(SESSION_ID);

        long distinctIds = result.campaigns().stream().map(OnboardingRecommendResponse.CampaignItem::id).distinct().count();
        assertThat(distinctIds).isEqualTo(result.campaigns().size());
    }

    // --- helpers ---

    private OnboardingResponse completeSession(String step1, String step2, String step3, String step4) {
        OnboardingResponse r = OnboardingResponse.create(SESSION_ID);
        r.applyStep(1, step1);
        r.applyStep(2, step2);
        r.applyStep(3, step3);
        r.applyStep(4, step4);
        r.updateRegionIds(List.of(1L));
        r.updateActivityTypes(List.of("NAVER"));
        return r;
    }

    private List<Campaign> buildCampaigns(int count, String category, String campaignType) {
        List<Campaign> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(buildCampaign((long) i, category, campaignType, null, null, null));
        }
        return list;
    }

    private Campaign buildCampaign(Long id, String category, String campaignType,
                                    Boolean isGuaranteed, Integer rewardAmount, LocalDate applyEndDate) {
        Campaign c = mock(Campaign.class);
        given(c.getId()).willReturn(id);
        given(c.getTitle()).willReturn("테스트 캠페인 " + id);
        given(c.getCategory()).willReturn(category == null ? null : CampaignCategory.valueOf(category));
        given(c.getType()).willReturn(campaignType == null ? null : CampaignType.valueOf(campaignType));
        given(c.getIsGuaranteed()).willReturn(isGuaranteed);
        given(c.getViewCount()).willReturn(rewardAmount == null ? null : rewardAmount.longValue());
        given(c.getRecruitCount()).willReturn(rewardAmount);
        given(c.getApplyEndDate()).willReturn(applyEndDate);
        return c;
    }
}
