package com.ject6.boost.domain.user.application.service;

import com.ject6.boost.common.exception.BusinessException;
import com.ject6.boost.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.domain.user.application.exception.UserErrorCode;
import com.ject6.boost.domain.user.domain.constant.ActivityType;
import com.ject6.boost.domain.user.domain.constant.BlogPlatform;
import com.ject6.boost.domain.user.domain.constant.CategoryType;
import com.ject6.boost.domain.user.domain.constant.NicknamePrefix;
import com.ject6.boost.domain.user.domain.constant.NicknameSuffix;
import com.ject6.boost.domain.user.domain.entity.Region;
import com.ject6.boost.domain.user.domain.entity.User;
import com.ject6.boost.domain.user.domain.entity.UserBlog;
import com.ject6.boost.domain.user.domain.entity.UserRegion;
import com.ject6.boost.domain.user.domain.repository.BlogAnalysisResultRepository;
import com.ject6.boost.domain.user.domain.repository.RegionRepository;
import com.ject6.boost.domain.user.domain.repository.UserBlogRepository;
import com.ject6.boost.domain.user.domain.repository.UserOAuthAccountRepository;
import com.ject6.boost.domain.user.domain.repository.UserRegionRepository;
import com.ject6.boost.domain.user.domain.repository.UserRepository;
import com.ject6.boost.domain.user.infrastructure.BlogPostCountClient;
import com.ject6.boost.domain.user.presentation.dto.BlogLinkRequest;
import com.ject6.boost.domain.user.presentation.dto.BlogLinkResponse;
import com.ject6.boost.domain.user.presentation.dto.NicknameCheckResponse;
import com.ject6.boost.domain.user.presentation.dto.ProfileRequest;
import com.ject6.boost.domain.user.presentation.dto.ProfileResponse;
import com.ject6.boost.domain.user.presentation.dto.RandomNicknameResponse;
import com.ject6.boost.domain.user.presentation.dto.UserMeResponse;
import com.ject6.boost.domain.user.presentation.dto.UserProfileUpdateRequest;
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
    private static final int MIN_BLOG_POST_COUNT = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final UserRegionRepository userRegionRepository;
    private final UserBlogRepository userBlogRepository;
    private final UserOAuthAccountRepository userOAuthAccountRepository;
    private final BlogAnalysisResultRepository blogAnalysisResultRepository;
    private final BlogPostCountClient blogPostCountClient;

    /**
     * 인증된 사용자의 프로필 정보를 조회하는 함수.
     */
    @Transactional(readOnly = true)
    public UserMeResponse getMe(AuthenticatedUser principal) {
        User user = findUser(principal);
        return toUserMeResponse(user);
    }

    private UserMeResponse toUserMeResponse(User user) {
        List<CategoryType> categoryTypes = List.copyOf(user.getCategoryTypes());
        List<ActivityType> activityTypes = List.copyOf(user.getActivityTypes());
        List<Long> regionIds = userRegionRepository.findByUser(user).stream()
                .map(userRegion -> userRegion.getRegion().getId())
                .toList();
        List<BlogLinkResponse> blogs = userBlogRepository.findActiveByUser(user).stream()
                .map(BlogLinkResponse::from)
                .toList();

        return new UserMeResponse(
                user.getId(),
                user.getNickname(),
                user.isProfileCompleted(),
                categoryTypes,
                activityTypes,
                regionIds,
                blogs
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
     * 요청된 닉네임을 사용할 수 있는지 확인하는 함수.
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
     * 인증된 사용자의 프로필 정보를 수정하는 함수.
     */
    @Transactional
    public ProfileResponse createProfile(AuthenticatedUser principal, ProfileRequest request) {
        validateProfileRequest(request);

        User user = findUser(principal);
        List<CategoryType> categoryTypes = parseCategoryTypes(request.categoryTypes());
        List<ActivityType> activityTypes = parseActivityTypes(request.activityTypes());
        List<Long> regionIds = distinctOptional(request.regionIds());
        List<Region> regions = findRegions(regionIds);

        user.createProfile(validateNickname(user, request.nickname()));

        user.replaceCategoryTypes(categoryTypes);
        user.replaceActivityTypes(activityTypes);
        userRegionRepository.replaceAll(user, regions);

        return new ProfileResponse(
                user.getId(),
                user.getNickname(),
                user.isProfileCompleted(),
                categoryTypes,
                activityTypes,
                regionIds
        );
    }
    @Transactional
    public UserMeResponse updateProfile(AuthenticatedUser principal, UserProfileUpdateRequest request) {
        User user = findUser(principal);
        if (request == null) {
            return toUserMeResponse(user);
        }

        if (request.nickname() != null) {
            updateNickname(user, request.nickname());
        }
        if (request.interestCategories() != null) {
            List<CategoryType> categoryTypes = parseCategoryTypes(request.interestCategories());
            user.replaceCategoryTypes(categoryTypes);
        }
        if (request.channels() != null) {
            List<ActivityType> activityTypes = parseActivityTypes(request.channels());
            user.replaceActivityTypes(activityTypes);
        }
        if (request.regions() != null) {
            List<Long> regionIds = distinctOptional(request.regions());
            List<Region> regions = findRegions(regionIds);
            userRegionRepository.replaceAll(user, regions);
        }

        return toUserMeResponse(user);
    }

    @Transactional
    public BlogLinkResponse linkBlog(AuthenticatedUser principal, BlogLinkRequest request) {
        User user = findUser(principal);
        if (request == null || !StringUtils.hasText(request.blogUrl())) {
            throw new BusinessException(UserErrorCode.BLOG_URL_REQUIRED);
        }
        if (!StringUtils.hasText(request.platform())) {
            throw new BusinessException(UserErrorCode.BLOG_PLATFORM_REQUIRED);
        }

        String blogUrl = request.blogUrl().trim();
        if (!blogUrl.startsWith(HTTPS_URL_PREFIX)) {
            throw new BusinessException(UserErrorCode.INVALID_BLOG_URL);
        }

        BlogPlatform platform = parseBlogPlatform(request.platform());
        int postCount = blogPostCountClient.countPosts(blogUrl, platform);
        if (postCount < MIN_BLOG_POST_COUNT) {
            throw new BusinessException(UserErrorCode.BLOG_POST_COUNT_INSUFFICIENT);
        }

        UserBlog blog = userBlogRepository.findActiveByUserAndPlatform(user, platform)
                .map(existingBlog -> {
                    existingBlog.update(blogUrl);
                    return existingBlog;
                })
                .orElseGet(() -> userBlogRepository.save(UserBlog.create(user, blogUrl, platform)));

        return BlogLinkResponse.from(blog);
    }
    /**
     * 인증된 사용자를 탈퇴 처리하고 연결 데이터를 soft delete 하는 함수.
     */
    @Transactional
    public void withdraw(AuthenticatedUser principal) {
        User user = findUser(principal);
        OffsetDateTime deletedAt = OffsetDateTime.now();
        user.withdraw(deletedAt);
        userOAuthAccountRepository.softDeleteByUser(user, deletedAt);
        userBlogRepository.softDeleteByUser(user, deletedAt);
        userRegionRepository.softDeleteByUser(user, deletedAt);
        blogAnalysisResultRepository.softDeleteByUser(user, deletedAt);
    }

    private void updateNickname(User user, String nickname) {
        user.updateNickname(validateNickname(user, nickname));
    }

    private String validateNickname(User user, String nickname) {
        if (!StringUtils.hasText(nickname)) {
            throw new BusinessException(UserErrorCode.NICKNAME_REQUIRED);
        }
        String trimmedNickname = nickname.trim();
        int nicknameLength = trimmedNickname.length();
        if (nicknameLength < 2 || nicknameLength > 100) {
            throw new BusinessException(UserErrorCode.INVALID_NICKNAME_LENGTH);
        }
        if (!trimmedNickname.equals(user.getNickname())
                && userRepository.existsByNicknameAndDeletedAtIsNull(trimmedNickname)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }
        return trimmedNickname;
    }

    private User findUser(AuthenticatedUser principal) {
        if (principal == null || principal.userId() == null) {
            throw new BusinessException(UserErrorCode.AUTHENTICATED_USER_REQUIRED);
        }
        return userRepository.findActiveById(principal.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    private List<Region> findRegions(List<Long> regionIds) {
        List<Region> regions = regionRepository.findByIdIn(regionIds);
        if (regions.size() != regionIds.size()) {
            throw new BusinessException(UserErrorCode.REGION_NOT_FOUND);
        }
        return regions;
    }

    private void validateProfileRequest(ProfileRequest request) {
        if (request == null) {
            throw new BusinessException(UserErrorCode.NICKNAME_REQUIRED);
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

    private BlogPlatform parseBlogPlatform(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(UserErrorCode.BLOG_PLATFORM_REQUIRED);
        }
        try {
            return BlogPlatform.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(UserErrorCode.INVALID_BLOG_PLATFORM);
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
