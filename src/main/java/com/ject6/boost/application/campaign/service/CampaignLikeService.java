package com.ject6.boost.application.campaign.service;

import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.application.campaign.exception.CampaignErrorCode;
import com.ject6.boost.domain.campaign.constant.UserCampaignStatus;
import com.ject6.boost.domain.campaign.entity.UserCampaign;
import com.ject6.boost.domain.campaign.repository.CampaignRepository;
import com.ject6.boost.domain.campaign.repository.UserCampaignRepository;
import com.ject6.boost.presentation.campaign.dto.LikeAnalysisResponse;
import com.ject6.boost.presentation.campaign.dto.LikeToggleResponse;
import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.repository.BlogAnalysisResultRepository;
import com.ject6.boost.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignLikeService {

    private static final int MIN_LIKE_COUNT_FOR_ANALYSIS = 5;

    private final CampaignRepository campaignRepository;
    private final UserCampaignRepository userCampaignRepository;
    private final UserRepository userRepository;
    private final BlogAnalysisResultRepository blogAnalysisResultRepository;

    @Transactional
    public LikeToggleResponse toggleLike(Long userId, Long campaignId) {
        campaignRepository.findById(campaignId)
            .orElseThrow(() -> new BusinessException(CampaignErrorCode.CAMPAIGN_NOT_FOUND));

        Optional<UserCampaign> existing = userCampaignRepository
            .findByUserIdAndCampaignIdAndStatus(userId, campaignId, UserCampaignStatus.LIKED);

        boolean liked;
        if (existing.isPresent()) {
            userCampaignRepository.delete(existing.get());
            liked = false;
        } else {
            User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new BusinessException(CampaignErrorCode.CAMPAIGN_NOT_FOUND));
            userCampaignRepository.save(UserCampaign.create(user, campaignId, UserCampaignStatus.LIKED));
            liked = true;
        }

        long likeCount = userCampaignRepository.countByCampaignIdAndStatus(campaignId, UserCampaignStatus.LIKED);
        return new LikeToggleResponse(liked, likeCount);
    }

    @Transactional(readOnly = true)
    public LikeAnalysisResponse getLikeAnalysis(Long campaignId) {
        campaignRepository.findById(campaignId)
            .orElseThrow(() -> new BusinessException(CampaignErrorCode.CAMPAIGN_NOT_FOUND));

        long likeCount = userCampaignRepository.countByCampaignIdAndStatus(campaignId, UserCampaignStatus.LIKED);
        if (likeCount < MIN_LIKE_COUNT_FOR_ANALYSIS) {
            return LikeAnalysisResponse.insufficient(campaignId, likeCount);
        }

        List<Long> likerIds = userCampaignRepository
            .findByCampaignIdAndStatus(campaignId, UserCampaignStatus.LIKED)
            .stream()
            .map(uc -> uc.getUser().getId())
            .toList();

        List<BlogAnalysisResult> results = blogAnalysisResultRepository
            .findByUserIdInAndDeletedAtIsNull(likerIds);

        Map<String, Integer> categoryCount = new LinkedHashMap<>();
        List<String> keywords = new ArrayList<>();

        for (BlogAnalysisResult r : results) {
            Map<String, Object> result = r.getResult();
            if (result == null) continue;

            Object topCategories = result.get("top_categories");
            if (topCategories instanceof List<?> cats) {
                for (Object cat : cats) {
                    if (cat instanceof Map<?, ?> catMap) {
                        Object catName = catMap.get("category");
                        if (catName instanceof String name) {
                            categoryCount.merge(name, 1, Integer::sum);
                        }
                    }
                }
            }

            Object keyTopics = result.get("key_topics");
            if (keyTopics instanceof List<?> topics) {
                for (Object t : topics) {
                    if (t instanceof String topic && !keywords.contains(topic)) {
                        keywords.add(topic);
                    }
                }
            }
        }

        List<LikeAnalysisResponse.CategoryStat> topCategories = categoryCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .map(e -> new LikeAnalysisResponse.CategoryStat(e.getKey(), e.getValue()))
            .toList();

        return new LikeAnalysisResponse(campaignId, likeCount, true, topCategories,
            keywords.stream().limit(10).toList());
    }
}
