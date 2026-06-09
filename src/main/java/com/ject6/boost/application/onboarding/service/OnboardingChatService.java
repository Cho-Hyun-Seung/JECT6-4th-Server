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
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepRequest;
import com.ject6.boost.presentation.onboarding.dto.OnboardingStepResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingChatService {

    private static final int MAX_RECOMMENDATIONS = 8;
    private static final int FALLBACK_FETCH_LIMIT = 20;

    private final OnboardingResponseRepository onboardingResponseRepository;
    private final CampaignRepository campaignRepository;

    @Transactional
    public OnboardingStepResponse saveStep(OnboardingStepRequest request) {
        if (request.step() < 1 || request.step() > 4) {
            throw new BusinessException(OnboardingErrorCode.INVALID_STEP);
        }

        String requestedSessionId = request.sessionId();
        String resolvedSessionId = (requestedSessionId == null || requestedSessionId.isBlank())
                ? UUID.randomUUID().toString()
                : requestedSessionId;

        OnboardingResponse response = onboardingResponseRepository.findBySessionId(resolvedSessionId)
                .orElseGet(() -> OnboardingResponse.create(resolvedSessionId));

        response.applyStep(request.step(), request.answer());
        onboardingResponseRepository.save(response);

        boolean complete = response.isComplete();
        Integer nextStep = complete ? null : request.step() + 1;
        return new OnboardingStepResponse(resolvedSessionId, request.step(), complete, nextStep);
    }

    @Transactional(readOnly = true)
    public OnboardingRecommendResponse getRecommendations(String sessionId) {
        OnboardingResponse response = onboardingResponseRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException(OnboardingErrorCode.SESSION_NOT_FOUND));

        if (!response.isComplete()) {
            throw new BusinessException(OnboardingErrorCode.ONBOARDING_NOT_COMPLETE);
        }

        CampaignCategory category = normalizeCategory(response.getStep1Answer());
        CampaignType campaignType = normalizeCampaignType(response.getStep3Answer());
        String activityLevel = response.getStep4Answer();

        List<Campaign> candidates = buildCandidates(category, campaignType);
        List<Campaign> sorted = sortByActivityLevel(candidates, activityLevel);

        List<OnboardingRecommendResponse.CampaignItem> items = sorted.stream()
                .limit(MAX_RECOMMENDATIONS)
                .map(c -> new OnboardingRecommendResponse.CampaignItem(
                        c.getId(), c.getTitle(),
                        c.getCategory() != null ? c.getCategory().name() : null,
                        c.getThumbnailUrl(), c.getApplyEndDate()))
                .toList();

        return new OnboardingRecommendResponse(sessionId, items);
    }

    @Transactional
    public void mergeSession(String sessionId, Long userId) {
        onboardingResponseRepository.findBySessionId(sessionId)
                .ifPresent(r -> {
                    r.mergeUser(userId);
                    onboardingResponseRepository.save(r);
                });
    }

    /**
     * 우선순위별로 추천 후보를 수집하고 중복을 제거한다.
     * 1순위: 카테고리 + 협찬 유형 모두 일치
     * 2순위: 카테고리만 일치
     * 3순위: 전체 활성 캠페인 fallback
     */
    private List<Campaign> buildCandidates(CampaignCategory category, CampaignType campaignType) {
        Set<Long> seen = new LinkedHashSet<>();
        List<Campaign> result = new ArrayList<>();

        if (category != null && campaignType != null) {
            for (Campaign c : campaignRepository.findActiveByCategoryAndType(category, campaignType)) {
                if (seen.add(c.getId())) result.add(c);
            }
        }

        if (category != null && result.size() < MAX_RECOMMENDATIONS) {
            for (Campaign c : campaignRepository.findActiveByCategory(category)) {
                if (seen.add(c.getId())) result.add(c);
            }
        }

        if (result.size() < MAX_RECOMMENDATIONS) {
            for (Campaign c : campaignRepository.findActiveFallback(FALLBACK_FETCH_LIMIT)) {
                if (seen.add(c.getId())) result.add(c);
            }
        }

        return result;
    }

    /**
     * 활동 수준(step4Answer)에 따라 추천 후보를 정렬한다.
     * BEGINNER: 보장형 공고 우선, 마감 여유 순
     * ACTIVE:   보상금액 높은 순, 모집 인원 많은 순
     * MIDDLE/기타: 마감일 가까운 순 (최신 공고 우선)
     */
    private List<Campaign> sortByActivityLevel(List<Campaign> campaigns, String activityLevel) {
        String level = activityLevel == null ? "" : activityLevel.trim().toUpperCase(Locale.ROOT);
        Comparator<Campaign> comparator = switch (level) {
            case "BEGINNER" -> Comparator
                    .comparingInt((Campaign c) -> Boolean.TRUE.equals(c.getIsGuaranteed()) ? 0 : 1)
                    .thenComparing(c -> c.getApplyEndDate() == null ? LocalDate.MAX : c.getApplyEndDate());
            case "ACTIVE" -> Comparator
                    .<Campaign, Long>comparing(
                            c -> c.getViewCount() == null ? 0L : c.getViewCount(),
                            Comparator.reverseOrder()
                    )
                    .thenComparing(
                            c -> c.getRecruitCount() == null ? 0 : c.getRecruitCount(),
                            Comparator.reverseOrder()
                    );
            default -> Comparator
                    .comparing((Campaign c) -> c.getApplyEndDate() == null ? LocalDate.MAX : c.getApplyEndDate())
                    .thenComparingLong(c -> -(c.getId() == null ? 0L : c.getId()));
        };
        return campaigns.stream().sorted(comparator).toList();
    }

    private CampaignCategory normalizeCategory(String step1Answer) {
        if (step1Answer == null || step1Answer.isBlank()) return null;
        return switch (step1Answer.trim().toUpperCase(Locale.ROOT)) {
            case "FOOD", "맛집", "카페", "맛집/카페"         -> CampaignCategory.FOOD;
            case "BEAUTY", "뷰티", "뷰티/패션"              -> CampaignCategory.BEAUTY;
            case "FASHION", "패션"                         -> CampaignCategory.FASHION;
            case "TRAVEL", "여행", "숙소", "여행/숙소"       -> CampaignCategory.TRAVEL;
            case "TECH", "TECH_IT", "IT", "제품", "IT/제품" -> CampaignCategory.TECH;
            case "LIFE", "LIVING", "LIFESTYLE",
                 "라이프", "생활", "라이프/기타"              -> CampaignCategory.LIFE;
            case "PET", "펫", "반려동물"                    -> CampaignCategory.PET;
            case "CULTURE", "문화"                         -> CampaignCategory.CULTURE;
            case "HEALTH", "EDUCATION"                     -> CampaignCategory.ETC;
            default                                        -> CampaignCategory.ETC;
        };
    }

    private CampaignType normalizeCampaignType(String step3Answer) {
        if (step3Answer == null || step3Answer.isBlank()) return null;
        return switch (step3Answer.trim().toUpperCase(Locale.ROOT)) {
            case "VISIT"    -> CampaignType.VISIT;
            case "DELIVERY" -> CampaignType.DELIVERY;
            case "PAID"     -> CampaignType.PAYBACK;
            default         -> null;
        };
    }
}
