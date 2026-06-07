package com.ject6.boost.domain.onboarding.application.service;

import com.ject6.boost.common.exception.BusinessException;
import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import com.ject6.boost.domain.campaign.domain.repository.CampaignRepository;
import com.ject6.boost.domain.onboarding.application.exception.OnboardingErrorCode;
import com.ject6.boost.domain.onboarding.domain.entity.OnboardingResponse;
import com.ject6.boost.domain.onboarding.domain.repository.OnboardingResponseRepository;
import com.ject6.boost.domain.onboarding.presentation.dto.OnboardingRecommendResponse;
import com.ject6.boost.domain.onboarding.presentation.dto.OnboardingStepRequest;
import com.ject6.boost.domain.onboarding.presentation.dto.OnboardingStepResponse;
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

        String category = normalizeCategory(response.getStep1Answer());
        String campaignType = normalizeCampaignType(response.getStep3Answer());
        String activityLevel = response.getStep4Answer();

        List<Campaign> candidates = buildCandidates(category, campaignType);
        List<Campaign> sorted = sortByActivityLevel(candidates, activityLevel);

        List<OnboardingRecommendResponse.CampaignItem> items = sorted.stream()
                .limit(MAX_RECOMMENDATIONS)
                .map(c -> new OnboardingRecommendResponse.CampaignItem(
                        c.getId(), c.getTitle(), c.getCategory(),
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
    private List<Campaign> buildCandidates(String category, String campaignType) {
        Set<Long> seen = new LinkedHashSet<>();
        List<Campaign> result = new ArrayList<>();

        if (campaignType != null) {
            for (Campaign c : campaignRepository.findActiveByCategoryAndType(category, campaignType)) {
                if (seen.add(c.getId())) result.add(c);
            }
        }

        if (result.size() < MAX_RECOMMENDATIONS) {
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
                    .<Campaign, Integer>comparing(
                            c -> c.getRewardAmount() == null ? 0 : c.getRewardAmount(),
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

    private String normalizeCategory(String step1Answer) {
        if (step1Answer == null || step1Answer.isBlank()) return "";
        return step1Answer.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * step3Answer(협찬 선호)를 Campaign.campaignType 값으로 변환한다.
     * VISIT → VISIT, DELIVERY → DELIVERY, PAID → PAYBACK, ANY → null(필터 없음)
     */
    private String normalizeCampaignType(String step3Answer) {
        if (step3Answer == null || step3Answer.isBlank()) return null;
        return switch (step3Answer.trim().toUpperCase(Locale.ROOT)) {
            case "VISIT"    -> "VISIT";
            case "DELIVERY" -> "DELIVERY";
            case "PAID"     -> "PAYBACK";
            default         -> null;
        };
    }
}
