package com.ject6.boost.application.my.service;

import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.domain.campaign.constant.CampaignApplyStatus;
import com.ject6.boost.domain.campaign.constant.UserCampaignStatus;
import com.ject6.boost.domain.campaign.entity.Campaign;
import com.ject6.boost.domain.campaign.entity.UserCampaign;
import com.ject6.boost.domain.campaign.entity.UserCampaignApply;
import com.ject6.boost.domain.campaign.entity.UserCampaignLike;
import com.ject6.boost.domain.campaign.repository.CampaignRepository;
import com.ject6.boost.domain.campaign.repository.UserCampaignApplyRepository;
import com.ject6.boost.domain.campaign.repository.UserCampaignLikeRepository;
import com.ject6.boost.domain.campaign.repository.UserCampaignRepository;
import com.ject6.boost.application.my.exception.MyErrorCode;
import com.ject6.boost.domain.my.entity.PointTransaction;
import com.ject6.boost.domain.my.entity.PointWallet;
import com.ject6.boost.domain.my.repository.PointTransactionRepository;
import com.ject6.boost.domain.my.repository.PointWalletRepository;
import com.ject6.boost.presentation.my.dto.*;
import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.repository.BlogAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MyService {

    private static final int FREE_PLAN_VISIBLE_COUNT = 3;
    private static final int MIN_WITHDRAW_AMOUNT = 5000;

    private final UserCampaignRepository userCampaignRepository;
    private final UserCampaignApplyRepository userCampaignApplyRepository;
    private final UserCampaignLikeRepository userCampaignLikeRepository;
    private final CampaignRepository campaignRepository;
    private final BlogAnalysisResultRepository blogAnalysisResultRepository;
    private final PointWalletRepository pointWalletRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional(readOnly = true)
    public List<MyCampaignListResponse> getMyCampaigns(Long userId, CampaignApplyStatus status) {
        List<UserCampaignApply> userCampaigns = status == null
                ? userCampaignApplyRepository.findByUserId(userId)
                : userCampaignApplyRepository.findByUserIdAndStatus(userId, status);

        List<Long> campaignIds = userCampaigns.stream().map(UserCampaignApply::getCampaignId).toList();
        Map<Long, Campaign> campaignMap = campaignRepository.findAllByIdIn(campaignIds)
                .stream().collect(Collectors.toMap(Campaign::getId, c -> c));

        return userCampaigns.stream()
                .map(uc -> {
                    Campaign c = campaignMap.get(uc.getCampaignId());
                    String title = c != null ? c.getTitle() : "삭제된 공고";
                    String brand = c != null ? c.getBrandName() : "";
                    return MyCampaignListResponse.from(uc, title, brand);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public MyCampaignListResponse getMyCampaignDetail(Long userId, Long userCampaignId) {
        UserCampaignApply uc = userCampaignApplyRepository.findById(userCampaignId)
                .filter(u -> u.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(MyErrorCode.USER_CAMPAIGN_NOT_FOUND));
        Campaign c = campaignRepository.findActiveById(uc.getCampaignId()).orElse(null);
        String title = c != null ? c.getTitle() : "삭제된 공고";
        String brand = c != null ? c.getBrandName() : "";
        return MyCampaignListResponse.from(uc, title, brand);
    }

    @Transactional(readOnly = true)
    public List<CampaignSummaryResponse> getRecentViews(Long userId) {
        return toCampaignSummaries(userCampaignRepository.findByUserIdAndStatus(userId, UserCampaignStatus.VIEWED));
    }

    @Transactional(readOnly = true)
    public List<CampaignSummaryResponse> getLikes(Long userId) {
        return toCampaignSummariesFromLikes(userCampaignLikeRepository.findByUserId(userId));
    }

    private List<CampaignSummaryResponse> toCampaignSummaries(List<UserCampaign> userCampaigns) {
        List<Long> ids = userCampaigns.stream().map(UserCampaign::getCampaignId).toList();
        Map<Long, Campaign> map = campaignRepository.findAllByIdIn(ids)
                .stream().collect(Collectors.toMap(Campaign::getId, c -> c));
        return userCampaigns.stream()
                .filter(uc -> map.containsKey(uc.getCampaignId()))
                .map(uc -> CampaignSummaryResponse.from(map.get(uc.getCampaignId())))
                .toList();
    }

    private List<CampaignSummaryResponse> toCampaignSummariesFromLikes(List<UserCampaignLike> likes) {
        List<Long> ids = likes.stream().map(UserCampaignLike::getCampaignId).toList();
        Map<Long, Campaign> map = campaignRepository.findAllByIdIn(ids)
                .stream().collect(Collectors.toMap(Campaign::getId, c -> c));
        return likes.stream()
                .filter(like -> map.containsKey(like.getCampaignId()))
                .map(like -> CampaignSummaryResponse.from(map.get(like.getCampaignId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnalysisHistoryItemResponse> getAnalysisHistory(Long userId, boolean isPremium) {
        List<BlogAnalysisResult> results = blogAnalysisResultRepository.findByUserIdAndDeletedAtIsNull(userId);
        return IntStream.range(0, results.size())
                .mapToObj(i -> {
                    boolean locked = !isPremium && i >= FREE_PLAN_VISIBLE_COUNT;
                    return AnalysisHistoryItemResponse.from(results.get(i), locked);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public PointBalanceResponse getPoints(Long userId) {
        PointWallet wallet = pointWalletRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(MyErrorCode.POINT_WALLET_NOT_FOUND));
        List<PointTransaction> transactions = pointTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return PointBalanceResponse.of(wallet, transactions);
    }

    @Transactional
    public PointWithdrawResponse withdraw(Long userId, PointWithdrawRequest request) {
        PointWallet wallet = pointWalletRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(MyErrorCode.POINT_WALLET_NOT_FOUND));
        if (request.amount() < MIN_WITHDRAW_AMOUNT) {
            throw new BusinessException(MyErrorCode.BELOW_MINIMUM_WITHDRAW);
        }
        if (wallet.getBalance() < request.amount()) {
            throw new BusinessException(MyErrorCode.INSUFFICIENT_BALANCE);
        }
        wallet.deduct(request.amount());
        pointWalletRepository.save(wallet);

        PointTransaction tx = PointTransaction.ofWithdraw(
                wallet.getUser(), request.amount(), wallet.getBalance(),
                request.bankName(), request.accountNumber(), request.accountHolder()
        );
        PointTransaction saved = pointTransactionRepository.save(tx);

        String masked = maskAccountNumber(request.accountNumber());
        return new PointWithdrawResponse(saved.getId(), request.amount(), "PENDING",
                request.bankName(), masked, saved.getCreatedAt());
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return "****";
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
