package com.ject6.boost.application.campaign.service;

import com.ject6.boost.domain.campaign.repository.CampaignRepository;
import com.ject6.boost.presentation.campaign.dto.CampaignListResponse;
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
