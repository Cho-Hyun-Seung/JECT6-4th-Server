package com.ject6.boost.infrastructure.campaign.repository;

import com.ject6.boost.domain.campaign.entity.UserCampaignLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCampaignLikeJpaRepository extends JpaRepository<UserCampaignLike, Long> {
    Optional<UserCampaignLike> findByUserIdAndCampaignId(Long userId, Long campaignId);
    List<UserCampaignLike> findByUserId(Long userId);
    List<UserCampaignLike> findByCampaignId(Long campaignId);

    @Query("""
        select ucl.campaignId
        from UserCampaignLike ucl
        where ucl.user.id = :userId
            and ucl.campaignId in :campaignIds
        """)
    List<Long> findCampaignIdsByUserIdAndCampaignIdIn(
            @Param("userId") Long userId,
            @Param("campaignIds") List<Long> campaignIds);

    boolean existsByUserIdAndCampaignId(Long userId, Long campaignId);
    long countByCampaignId(Long campaignId);
}
