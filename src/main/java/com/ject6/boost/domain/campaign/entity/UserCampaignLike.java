package com.ject6.boost.domain.campaign.entity;

import com.ject6.boost.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Entity
@Table(
        name = "user_campaign_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_campaign_like_user_campaign",
                columnNames = {"user_id", "campaign_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCampaignLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static UserCampaignLike create(User user, Long campaignId) {
        UserCampaignLike like = new UserCampaignLike();
        like.user = user;
        like.campaignId = campaignId;
        return like;
    }
}
