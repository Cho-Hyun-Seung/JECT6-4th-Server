package com.ject6.boost.domain.user.domain.entity;

import com.ject6.boost.domain.user.domain.constant.ActivityType;
import com.ject6.boost.domain.user.domain.constant.CategoryType;
import com.ject6.boost.domain.user.domain.constant.SubscriptionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false, length = 50)
    private SubscriptionType subscriptionType = SubscriptionType.FREE;

    @Column(name = "ai_credit_remaining", nullable = false)
    private Integer aiCreditRemaining = 3;

    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCategory> userCategories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserActivityType> userActivityTypes = new LinkedHashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    /**
     * 프로필 전 기본 사용자 엔티티를 생성하는 함수.
     */
    public static User create() {
        return new User();
    }

    public SubscriptionType getSubscriptionTypeOrDefault() {
        return subscriptionType == null ? SubscriptionType.FREE : subscriptionType;
    }

    public int getAiCreditRemainingOrDefault() {
        return aiCreditRemaining == null ? 3 : aiCreditRemaining;
    }

    public void updateOAuthProfile(String nickname, String profileImageUrl) {
        if (this.nickname == null && nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    /**
     * 프로필 입력 닉네임을 저장하고 프로필 완료 상태로 변경하는 함수.
     */
    public void createProfile(String nickname) {
        this.nickname = nickname;
        this.profileCompleted = true;
    }

    public Set<CategoryType> getCategoryTypes() {
        Set<CategoryType> categoryTypes = new LinkedHashSet<>();
        for (UserCategory userCategory : userCategories) {
            categoryTypes.add(userCategory.getCategoryType());
        }
        return categoryTypes;
    }

    public void replaceCategoryTypes(List<CategoryType> categoryTypes) {
        this.userCategories.clear();
        new LinkedHashSet<>(categoryTypes).forEach(categoryType ->
                this.userCategories.add(UserCategory.create(this, categoryType)));
    }

    public Set<ActivityType> getActivityTypes() {
        Set<ActivityType> activityTypes = new LinkedHashSet<>();
        for (UserActivityType userActivityType : userActivityTypes) {
            activityTypes.add(userActivityType.getActivityType());
        }
        return activityTypes;
    }

    public void replaceActivityTypes(List<ActivityType> activityTypes) {
        this.userActivityTypes.clear();
        new LinkedHashSet<>(activityTypes).forEach(activityType ->
                this.userActivityTypes.add(UserActivityType.create(this, activityType)));
    }

    /**
     * 사용자 프로필 닉네임을 수정하는 함수.
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 사용자를 탈퇴 상태로 변경하는 함수.
     */
    public void withdraw(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
        this.userCategories.clear();
        this.userActivityTypes.clear();
    }
}
