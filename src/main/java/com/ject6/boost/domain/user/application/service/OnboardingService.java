package com.ject6.boost.domain.user.application.service;

import com.ject6.boost.common.exception.BusinessException;
import com.ject6.boost.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.domain.user.application.exception.UserErrorCode;
import com.ject6.boost.domain.user.domain.constant.ActivityType;
import com.ject6.boost.domain.user.domain.constant.CategoryType;
import com.ject6.boost.domain.user.domain.constant.NicknamePrefix;
import com.ject6.boost.domain.user.domain.constant.NicknameSuffix;
import com.ject6.boost.domain.user.domain.entity.Region;
import com.ject6.boost.domain.user.domain.entity.User;
import com.ject6.boost.domain.user.domain.repository.RegionRepository;
import com.ject6.boost.domain.user.domain.repository.UserActivityTypeRepository;
import com.ject6.boost.domain.user.domain.repository.UserCategoryRepository;
import com.ject6.boost.domain.user.domain.repository.UserRegionRepository;
import com.ject6.boost.domain.user.domain.repository.UserRepository;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileRequest;
import com.ject6.boost.domain.user.presentation.dto.OnboardingProfileResponse;
import com.ject6.boost.domain.user.presentation.dto.RandomNicknameResponse;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserActivityTypeRepository userActivityTypeRepository;
    private final UserRegionRepository userRegionRepository;

    /**
     * 온보딩에서 사용할 랜덤 닉네임 후보를 생성하는 함수.
     */
    public RandomNicknameResponse generateRandomNickname() {
        String prefix = NicknamePrefix.randomValue(RANDOM);
        String suffix = NicknameSuffix.randomValue(RANDOM);
        int number = RANDOM.nextInt(9000) + 1000;
        return new RandomNicknameResponse(prefix + " " + suffix + number);
    }

    /**
     * 인증된 사용자의 온보딩 프로필 정보를 저장하는 함수.
     */
    @Transactional
    public OnboardingProfileResponse updateProfile(AuthenticatedUser principal, OnboardingProfileRequest request) {
        validatePrincipal(principal);
        validateRequest(request);

        User user = userRepository.findActiveById(principal.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        List<CategoryType> categoryTypes = parseCategoryTypes(request.categoryTypes());
        List<ActivityType> activityTypes = parseActivityTypes(request.activityTypes());
        List<Long> regionIds = distinctOptional(request.regionIds());
        List<Region> regions = findRegions(regionIds);

        user.completeOnboarding(request.nickname().trim());

        userCategoryRepository.replaceAll(user, categoryTypes);
        userActivityTypeRepository.replaceAll(user, activityTypes);
        userRegionRepository.replaceAll(user, regions);

        return new OnboardingProfileResponse(
                user.getId(),
                user.getNickname(),
                user.isOnboardingCompleted(),
                categoryTypes,
                activityTypes,
                regionIds
        );
    }

    /**
     * 요청한 지역 id 목록을 조회하고 모두 존재하는지 검증하는 함수.
     */
    private List<Region> findRegions(List<Long> regionIds) {
        List<Region> regions = regionRepository.findByIdIn(regionIds);
        if (regions.size() != regionIds.size()) {
            throw new BusinessException(UserErrorCode.REGION_NOT_FOUND);
        }
        return regions;
    }

    /**
     * 인증 principal에 사용자 id가 포함되어 있는지 검증하는 함수.
     */
    private void validatePrincipal(AuthenticatedUser principal) {
        if (principal == null || principal.userId() == null) {
            throw new BusinessException(UserErrorCode.AUTHENTICATED_USER_REQUIRED);
        }
    }

    /**
     * 온보딩 프로필 요청의 필수 입력값을 검증하는 함수.
     */
    private void validateRequest(OnboardingProfileRequest request) {
        if (request == null || !StringUtils.hasText(request.nickname())) {
            throw new BusinessException(UserErrorCode.NICKNAME_REQUIRED);
        }
        int nicknameLength = request.nickname().trim().length();
        if (nicknameLength < 2 || nicknameLength > 100) {
            throw new BusinessException(UserErrorCode.INVALID_NICKNAME_LENGTH);
        }
        if (request.categoryTypes() == null || request.categoryTypes().isEmpty()) {
            throw new BusinessException(UserErrorCode.CATEGORY_REQUIRED);
        }
        if (request.activityTypes() == null || request.activityTypes().isEmpty()) {
            throw new BusinessException(UserErrorCode.ACTIVITY_TYPE_REQUIRED);
        }
    }

    private List<CategoryType> parseCategoryTypes(List<String> categoryTypeValues) {
        return distinct(categoryTypeValues.stream()
                .map(this::parseCategoryType)
                .toList());
    }

    private CategoryType parseCategoryType(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(UserErrorCode.CATEGORY_REQUIRED);
        }
        try {
            return CategoryType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(UserErrorCode.INVALID_CATEGORY_TYPE);
        }
    }

    private List<ActivityType> parseActivityTypes(List<String> activityTypeValues) {
        return distinct(activityTypeValues.stream()
                .map(this::parseActivityType)
                .toList());
    }

    private ActivityType parseActivityType(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(UserErrorCode.ACTIVITY_TYPE_REQUIRED);
        }
        try {
            return ActivityType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(UserErrorCode.INVALID_ACTIVITY_TYPE);
        }
    }

    /**
     * 요청 순서를 유지하면서 중복 값을 제거하는 함수.
     */
    private <T> List<T> distinct(List<T> values) {
        Set<T> distinctValues = new LinkedHashSet<>(values);
        return new ArrayList<>(distinctValues);
    }

    private <T> List<T> distinctOptional(List<T> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return distinct(values);
    }
}
