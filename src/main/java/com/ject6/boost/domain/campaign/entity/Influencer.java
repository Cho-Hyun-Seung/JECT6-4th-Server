package com.ject6.boost.domain.campaign.entity;

import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "influencer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Influencer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String influencerName; // 인플루언서 이름 또는 id ex) toki0327

    private String blogName; // 블로그 이름

    private String title;   // 포스팅 제목

    private String thumbnailUrl;    // 썸네일 이미지

    private String blogUrl; // 블로그 URL

    @Enumerated(EnumType.STRING)
    private CampaignCategory category;  // 카테고리
}
