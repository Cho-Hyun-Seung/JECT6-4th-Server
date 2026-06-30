package com.ject6.boost.application.campaign.service;

import com.ject6.boost.domain.campaign.constant.CampaignCategory;
import com.ject6.boost.domain.campaign.repository.InfluencerRepository;
import com.ject6.boost.presentation.campaign.dto.BloggerStoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignFeedService {

    private static final int BLOGGER_STORY_LIMIT = 10;

    private final InfluencerRepository influencerRepository;

    public List<BloggerStoryResponse> getBloggerStories(CampaignCategory category) {
        return influencerRepository.findFeedStories(category)
                .stream()
                .map(BloggerStoryResponse::from)
                .toList();
    }
}
