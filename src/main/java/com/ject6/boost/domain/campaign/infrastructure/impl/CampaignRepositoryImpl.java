package com.ject6.boost.domain.campaign.infrastructure.impl;

import com.ject6.boost.domain.campaign.domain.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.domain.constant.CampaignStatus;
import com.ject6.boost.domain.campaign.domain.constant.CampaignType;
import com.ject6.boost.domain.campaign.domain.constant.SortType;
import com.ject6.boost.domain.campaign.domain.entity.Campaign;
import com.ject6.boost.domain.campaign.domain.entity.QCampaign;
import com.ject6.boost.domain.campaign.domain.repository.CampaignRepository;
import com.ject6.boost.domain.campaign.infrastructure.repository.CampaignJpaRepository;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignFilterRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepository {

    private final CampaignJpaRepository jpaRepository;
    private final JPAQueryFactory queryFactory;
    private final QCampaign c = QCampaign.campaign;

    @Override
    public Optional<Campaign> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Campaign> findActiveById(Long id) {
        Campaign result = queryFactory.selectFrom(c)
            .where(c.id.eq(id)
                .and(c.deletedAt.isNull())
                .and(c.status.eq(CampaignStatus.ACTIVE)))
            .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<Campaign> findAllByIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return jpaRepository.findByIdIn(ids);
    }

    @Override
    public Page<Campaign> search(CampaignFilterRequest filter, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(c.deletedAt.isNull());

        if (filter.getCategories() != null && !filter.getCategories().isEmpty()) {
            builder.and(c.category.in(filter.getCategories()));
        }
        if (filter.getRegion() != null) {
            builder.and(c.region.eq(filter.getRegion()));
        }
        if (filter.getChannel() != null) {
            builder.and(c.channel.eq(filter.getChannel()));
        }
        if (filter.getSourcePlatform() != null) {
            builder.and(c.sourcePlatform.eq(filter.getSourcePlatform()));
        }
        if (filter.getType() != null) {
            builder.and(c.type.eq(filter.getType()));
        }

        OrderSpecifier<?> order = resolveOrder(filter.getSort());

        List<Campaign> content = queryFactory
            .selectFrom(c)
            .where(builder)
            .orderBy(order)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(c)
            .where(builder)
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Campaign> searchByKeyword(String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(c.deletedAt.isNull());
        builder.and(
            c.title.containsIgnoreCase(keyword)
                .or(c.brandName.containsIgnoreCase(keyword))
        );

        List<Campaign> content = queryFactory
            .selectFrom(c)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .selectFrom(c)
            .where(builder)
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Campaign> findRelated(Long campaignId, CampaignCategory category, int limit) {
        return jpaRepository
            .findTop3ByCategoryAndIdNotAndDeletedAtIsNull(category, campaignId);
    }

    @Override
    public List<Campaign> findPopular(int limit) {
        return jpaRepository.findTop10ByDeletedAtIsNullOrderByViewCountDesc();
    }

    @Override
    public List<Campaign> findGuaranteed(int limit) {
        return jpaRepository
            .findTop10ByIsGuaranteedTrueAndDeletedAtIsNullOrderByApplyEndDateAsc();
    }

    @Override
    public List<Campaign> findClosingSoon(int limit) {
        return jpaRepository
            .findTop10ByStatusAndApplyEndDateAfterAndDeletedAtIsNullOrderByApplyEndDateAsc(
                CampaignStatus.ACTIVE, LocalDate.now());
    }

    @Override
    public List<Campaign> findActiveByCategoryAndType(CampaignCategory category, CampaignType type) {
        LocalDate today = LocalDate.now();
        return queryFactory.selectFrom(c)
            .where(c.deletedAt.isNull()
                .and(c.status.eq(CampaignStatus.ACTIVE))
                .and(c.applyEndDate.isNull().or(c.applyEndDate.goe(today)))
                .and(c.category.eq(category))
                .and(c.type.eq(type)))
            .orderBy(c.createdAt.desc())
            .fetch();
    }

    @Override
    public List<Campaign> findActiveByCategory(CampaignCategory category) {
        LocalDate today = LocalDate.now();
        return queryFactory.selectFrom(c)
            .where(c.deletedAt.isNull()
                .and(c.status.eq(CampaignStatus.ACTIVE))
                .and(c.applyEndDate.isNull().or(c.applyEndDate.goe(today)))
                .and(c.category.eq(category)))
            .orderBy(c.createdAt.desc())
            .fetch();
    }

    @Override
    public List<Campaign> findActiveFallback(int limit) {
        LocalDate today = LocalDate.now();
        return queryFactory.selectFrom(c)
            .where(c.deletedAt.isNull()
                .and(c.status.eq(CampaignStatus.ACTIVE))
                .and(c.applyEndDate.isNull().or(c.applyEndDate.goe(today))))
            .orderBy(c.createdAt.desc())
            .limit(limit)
            .fetch();
    }

    private OrderSpecifier<?> resolveOrder(SortType sort) {
        if (sort == null) return c.createdAt.desc();
        return switch (sort) {
            case CLOSING     -> c.applyEndDate.asc();   // deadline → applyEndDate
            case COMPETITION -> c.applyCount.desc();
            case POPULAR     -> c.viewCount.desc();
        };
    }
}