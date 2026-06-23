package com.ject6.boost.presentation.campaign.controller;

import com.ject6.boost.application.campaign.service.CampaignService;
import com.ject6.boost.presentation.campaign.dto.CampaignBulkRequest;
import com.ject6.boost.presentation.common.dto.ApiResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/campaigns")
@RequiredArgsConstructor
@Profile("!mock")
public class InternalCampaignController {

    private final CampaignService campaignService;

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> bulkUpsert(
            @RequestBody CampaignBulkRequest request) {
        int saved = campaignService.bulkUpsert(request.campaigns());
        log.info("internal campaign bulk upsert: saved={}", saved);
        return ResponseEntity.ok(ApiResponse.success(Map.of("saved", saved)));
    }
}
