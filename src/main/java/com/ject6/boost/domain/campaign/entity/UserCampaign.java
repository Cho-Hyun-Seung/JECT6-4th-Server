package com.ject6.boost.domain.campaign.entity;

import com.ject6.boost.domain.campaign.constant.UserCampaignStatus;
import com.ject6.boost.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "user_campaigns")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCampaign {

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
    private UserCampaignStatus status;

    @Column(name = "applied_at")
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

    public static UserCampaign create(User user, Long campaignId, UserCampaignStatus status) {
        UserCampaign uc = new UserCampaign();
        uc.user = user;
        uc.campaignId = campaignId;
        uc.status = status;
        return uc;
    }

    public void updateStatus(UserCampaignStatus newStatus) {
        this.status = newStatus;
    }
}
