package com.ject6.boost.domain.campaign.entity;

import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.constant.CampaignStatus;
import com.ject6.boost.domain.campaign.constant.CampaignType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "campaigns")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourcePlatform;

    private String brandName;

    @Column(nullable = false)
    private String title;

    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private CampaignCategory category;

    @Enumerated(EnumType.STRING)
    private CampaignType type;

    private String channel;

    private String region;

    @Column(columnDefinition = "TEXT")
    private String providedContent;

    private Integer recruitCount;

    private Integer applyCount;

    private LocalDate applyStartDate;

    private LocalDate applyEndDate;

    private LocalDate announceDate;

    private LocalDate purchaseStartDate;

    private LocalDate purchaseEndDate;

    private LocalDate reviewDeadline;

    @Column(columnDefinition = "TEXT")
    private String mission;

    @Column(columnDefinition = "TEXT")
    private String searchKeywords;

    private Boolean isGuaranteed;

    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    @Column(unique = true)
    private String sourceUrl;

    private Long viewCount;

    private LocalDateTime crawledAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}