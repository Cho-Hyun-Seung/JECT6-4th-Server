package com.ject6.boost.domain.campaign.application.service;

import com.ject6.boost.domain.campaign.domain.repository.CampaignRepository;
import com.ject6.boost.domain.campaign.presentation.dto.CampaignListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignSearchService {

    private final CampaignRepository campaignRepository;

    public Page<CampaignListResponse> search(String keyword, Pageable pageable) {
        return campaignRepository.searchByKeyword(keyword, pageable)
            .map(CampaignListResponse::from);
    }
}
