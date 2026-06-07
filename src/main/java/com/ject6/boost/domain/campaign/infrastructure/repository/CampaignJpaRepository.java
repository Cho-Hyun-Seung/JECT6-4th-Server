package com.ject6.boost.domain.campaign.infrastructure.repository;

import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CampaignJpaRepository extends JpaRepository<Campaign, Long> {
    Optional<Campaign> findByIdAndDeletedAtIsNull(Long id);
    List<Campaign> findAllByIdInAndDeletedAtIsNull(List<Long> ids);

    @Query("""
            SELECT c FROM Campaign c
            WHERE c.deletedAt IS NULL
              AND (c.applyEndDate IS NULL OR c.applyEndDate >= :today)
              AND UPPER(c.category) = UPPER(:category)
              AND UPPER(c.campaignType) = UPPER(:campaignType)
            """)
    List<Campaign> findActiveByCategoryAndCampaignType(
            @Param("category") String category,
            @Param("campaignType") String campaignType,
            @Param("today") LocalDate today,
            Pageable pageable);

    @Query("""
            SELECT c FROM Campaign c
            WHERE c.deletedAt IS NULL
              AND (c.applyEndDate IS NULL OR c.applyEndDate >= :today)
              AND UPPER(c.category) = UPPER(:category)
            """)
    List<Campaign> findActiveByCategory(
            @Param("category") String category,
            @Param("today") LocalDate today,
            Pageable pageable);

    @Query("""
            SELECT c FROM Campaign c
            WHERE c.deletedAt IS NULL
              AND (c.applyEndDate IS NULL OR c.applyEndDate >= :today)
            ORDER BY c.createdAt DESC
            """)
    List<Campaign> findActiveCampaigns(@Param("today") LocalDate today, Pageable pageable);
}
