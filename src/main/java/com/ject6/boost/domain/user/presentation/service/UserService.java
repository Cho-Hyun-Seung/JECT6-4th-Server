package com.ject6.boost.domain.user.presentation.service;

import com.ject6.boost.common.exception.BusinessException;
import com.ject6.boost.common.security.AuthenticatedUser;
import com.ject6.boost.domain.user.application.dto.ActivityChannelRequest;
import com.ject6.boost.domain.user.application.dto.ActivityChannelResponse;
import com.ject6.boost.domain.user.application.dto.NicknameCheckResponse;
import com.ject6.boost.domain.user.application.dto.OnboardingProfileRequest;
import com.ject6.boost.domain.user.application.dto.OnboardingProfileResponse;
import com.ject6.boost.domain.user.application.dto.RandomNicknameResponse;
import com.ject6.boost.domain.user.application.dto.UserMeResponse;
import com.ject6.boost.domain.user.domain.constant.ActivityType;
import com.ject6.boost.domain.user.domain.constant.NicknamePrefix;
import com.ject6.boost.domain.user.domain.constant.NicknameSuffix;
import com.ject6.boost.domain.user.domain.entity.Category;
import com.ject6.boost.domain.user.domain.entity.Region;
import com.ject6.boost.domain.user.domain.entity.User;
import com.ject6.boost.domain.user.domain.entity.UserActivityType;
import com.ject6.boost.domain.user.domain.entity.UserRegion;
import com.ject6.boost.domain.user.infrastructure.repository.CategoryRepository;
import com.ject6.boost.domain.user.infrastructure.repository.RegionRepository;
import com.ject6.boost.domain.user.infrastructure.repository.UserActivityChannelRepository;
import com.ject6.boost.domain.user.infrastructure.repository.UserActivityTypeRepository;
import com.ject6.boost.domain.user.infrastructure.repository.UserCategoryRepository;
import com.ject6.boost.domain.user.infrastructure.repository.UserRegionRepository;
import com.ject6.boost.domain.user.infrastructure.repository.UserRepository;
import com.ject6.boost.domain.user.presentation.exception.UserErrorCode;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
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
public class UserService {

    private static final String HTTPS_URL_PREFIX = "https://";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RegionRepository regionRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserActivityTypeRepository userActivityTypeRepository;
    private final UserRegionRepository userRegionRepository;
    private final UserActivityChannelRepository userActivityChannelRepository;

    /**
     * 현재 인증된 사용자 정보를 조회하는 함수.
     */
    @Transactional(readOnly = true)
    public UserMeResponse getMe(AuthenticatedUser principal) {
        User user = findUser(principal);
        List<Long> categoryIds = userCategoryRepository.findByUser(user).stream()
                .map(userCategory -> userCategory.getCategory().getId())
                .toList();
        List<ActivityType> activityTypes = userActivityTypeRepository.findByUser(user).stream()
                .map(UserActivityType::getActivityType)
                .toList();
        List<Long> regionIds = userRegionRepository.findByUser(user).stream()
                .map(userRegion -> userRegion.getRegion().getId())
                .toList();
        List<ActivityChannelResponse> activityChannels = userActivityChannelRepository.findByUser(user).stream()
                .map(channel -> new ActivityChannelResponse(
                        channel.getId(),
                        channel.getActivityType(),
                        channel.getUrl()
                ))
                .toList();

        return new UserMeResponse(
                user.getId(),
                user.getNickname(),
                user.isOnboardingCompleted(),
                categoryIds,
                activityTypes,
                regionIds,
                activityChannels
        );
    }

    /**
     * 랜덤 닉네임 후보를 생성하는 함수.
     */
    public RandomNicknameResponse generateRandomNickname() {
        String prefix = NicknamePrefix.randomValue(RANDOM);
        String suffix = NicknameSuffix.randomValue(RANDOM);
        int number = RANDOM.nextInt(9000) + 1000;
        return new RandomNicknameResponse(prefix + " " + suffix + number);
    }

    /**
     * 닉네임 사용 가능 여부를 확인하는 함수.
     */
    public NicknameCheckResponse checkNickname(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            throw new BusinessException(UserErrorCode.NICKNAME_REQUIRED);
        }
        String trimmedNickname = nickname.trim();
        return new NicknameCheckResponse(
                trimmedNickname,
                !userRepository.existsByNicknameAndDeletedAtIsNull(trimmedNickname)
        );
    }

    /**
     * 현재 인증된 사용자의 프로필 정보를 수정하는 함수.
     */
    @Transactional
    public OnboardingProfileResponse updateProfile(AuthenticatedUser principal, OnboardingProfileRequest request) {
        validateRequest(request);

        User user = findUser(principal);
        List<Long> categoryIds = distinct(request.categoryIds());
        List<ActivityType> activityTypes = parseActivityTypes(request.activityTypes());
        List<Long> regionIds = distinctOptional(request.regionIds());
        List<Category> categories = findCategories(categoryIds);
        List<Region> regions = findRegions(regionIds);

        user.completeOnboarding(request.nickname().trim());

        userCategoryRepository.replaceAll(user, categories);
        userActivityTypeRepository.replaceAll(user, activityTypes);
        userRegionRepository.replaceAll(user, regions);

        return new OnboardingProfileResponse(
                user.getId(),
                user.getNickname(),
                user.isOnboardingCompleted(),
                categoryIds,
                activityTypes,
                regionIds
        );
    }

    /**
     * 현재 인증된 사용자의 활동 채널 URL을 연동하는 함수.
     */
    @Transactional
    public ActivityChannelResponse linkActivityChannel(AuthenticatedUser principal, ActivityChannelRequest request) {
        if (request == null || request.activityType() == null) {
            throw new BusinessException(UserErrorCode.ACTIVITY_CHANNEL_TYPE_REQUIRED);
        }
        ActivityType activityType = parseActivityType(request.activityType());
        if (!StringUtils.hasText(request.url())) {
            throw new BusinessException(UserErrorCode.ACTIVITY_CHANNEL_URL_REQUIRED);
        }
        String url = request.url().trim();
        if (!url.startsWith(HTTPS_URL_PREFIX)) {
            throw new BusinessException(UserErrorCode.INVALID_ACTIVITY_CHANNEL_URL);
        }

        User user = findUser(principal);
        var channel = userActivityChannelRepository.saveOrUpdate(
                user,
                activityType,
                url
        );

        return new ActivityChannelResponse(channel.getId(), channel.getActivityType(), channel.getUrl());
    }

    /**
     * 현재 인증된 사용자의 활동 채널 URL 연동을 해제하는 함수.
     */
    @Transactional
    public void unlinkActivityChannel(AuthenticatedUser principal, String activityTypeValue) {
        ActivityType activityType = parseActivityType(activityTypeValue);
        User user = findUser(principal);
        userActivityChannelRepository.deleteByUserAndActivityType(user, activityType);
    }

    /**
     * 현재 인증된 사용자를 탈퇴 처리하는 함수.
     */
    @Transactional
    public void withdraw(AuthenticatedUser principal) {
        User user = findUser(principal);
        user.withdraw(OffsetDateTime.now());
    }

    private User findUser(AuthenticatedUser principal) {
        if (principal == null || principal.userId() == null) {
            throw new BusinessException(UserErrorCode.AUTHENTICATED_USER_REQUIRED);
        }
        return userRepository.findById(principal.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private List<Category> findCategories(List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findByIdIn(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new BusinessException(UserErrorCode.CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    private List<Region> findRegions(List<Long> regionIds) {
        List<Region> regions = regionRepository.findByIdIn(regionIds);
        if (regions.size() != regionIds.size()) {
            throw new BusinessException(UserErrorCode.REGION_NOT_FOUND);
        }
        return regions;
    }

    private void validateRequest(OnboardingProfileRequest request) {
        if (request == null || !StringUtils.hasText(request.nickname())) {
            throw new BusinessException(UserErrorCode.NICKNAME_REQUIRED);
        }
        int nicknameLength = request.nickname().trim().length();
        if (nicknameLength < 2 || nicknameLength > 100) {
            throw new BusinessException(UserErrorCode.INVALID_NICKNAME_LENGTH);
        }
        if (request.categoryIds() == null || request.categoryIds().isEmpty()) {
            throw new BusinessException(UserErrorCode.CATEGORY_REQUIRED);
        }
        if (request.activityTypes() == null || request.activityTypes().isEmpty()) {
            throw new BusinessException(UserErrorCode.ACTIVITY_TYPE_REQUIRED);
        }
    }

    private List<ActivityType> parseActivityTypes(List<String> activityTypeValues) {
        return distinct(activityTypeValues).stream()
                .map(this::parseActivityType)
                .toList();
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
