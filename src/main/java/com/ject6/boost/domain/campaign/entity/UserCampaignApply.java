package com.ject6.boost.domain.campaign.entity;

import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Table(
        name = "user_campaign_apply",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_campaign_apply_user_campaign",
                columnNames = {"user_id", "campaign_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCampaignApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CampaignApplyStatus status;

    @Column(name = "applied_at", nullable = false)
    private OffsetDateTime appliedAt;

    @Column(name = "review_deadline")
    private LocalDate reviewDeadline;

    @Column(name = "reward_amount")
    private Integer rewardAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public static UserCampaignApply create(User user, Campaign campaign) {
        UserCampaignApply apply = new UserCampaignApply();
        apply.user = user;
        apply.campaignId = campaign.getId();
        apply.status = CampaignApplyStatus.APPLIED;
        apply.appliedAt = OffsetDateTime.now();
        apply.reviewDeadline = campaign.getReviewDeadline();
        return apply;
    }

    public void updateStatus(CampaignApplyStatus newStatus) {
        this.status = newStatus;
    }
}
