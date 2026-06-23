package com.ject6.boost.application.my.service;

import com.ject6.boost.application.common.exception.BusinessException;
import com.ject6.boost.application.my.exception.MyErrorCode;
import com.ject6.boost.application.user.exception.UserErrorCode;
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
import com.ject6.boost.domain.my.entity.PointTransaction;
import com.ject6.boost.domain.my.entity.PointWallet;
import com.ject6.boost.domain.my.repository.PointTransactionRepository;
import com.ject6.boost.domain.my.repository.PointWalletRepository;
import com.ject6.boost.domain.user.entity.BlogAnalysisResult;
import com.ject6.boost.domain.user.entity.User;
import com.ject6.boost.domain.user.entity.UserBlog;
import com.ject6.boost.domain.user.repository.BlogAnalysisResultRepository;
import com.ject6.boost.domain.user.repository.UserBlogRepository;
import com.ject6.boost.domain.user.repository.UserRepository;
import com.ject6.boost.presentation.my.dto.AnalysisHistoryItemResponse;
import com.ject6.boost.presentation.my.dto.CampaignSummaryResponse;
import com.ject6.boost.presentation.my.dto.MyAccountResponse;
import com.ject6.boost.presentation.my.dto.MyAiHistoryItemResponse;
import com.ject6.boost.presentation.my.dto.MyAiHistoryResponse;
import com.ject6.boost.presentation.my.dto.MyCampaignListResponse;
import com.ject6.boost.presentation.my.dto.MyCampaignSummaryResponse;
import com.ject6.boost.presentation.my.dto.MyRecentAppliedCampaignListResponse;
import com.ject6.boost.presentation.my.dto.MyRecentAppliedCampaignSummaryResponse;
import com.ject6.boost.presentation.my.dto.PointBalanceResponse;
import com.ject6.boost.presentation.my.dto.PointWithdrawRequest;
import com.ject6.boost.presentation.my.dto.PointWithdrawResponse;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyService {

    private static final int FREE_PLAN_VISIBLE_COUNT = 3;
    private static final int MIN_WITHDRAW_AMOUNT = 5000;
    private static final int RECENT_APPLIED_SUMMARY_COUNT = 3;

    private final UserRepository userRepository;
    private final UserBlogRepository userBlogRepository;
    private final UserCampaignRepository userCampaignRepository;
    private final UserCampaignApplyRepository userCampaignApplyRepository;
    private final UserCampaignLikeRepository userCampaignLikeRepository;
    private final CampaignRepository campaignRepository;
    private final BlogAnalysisResultRepository blogAnalysisResultRepository;
    private final PointWalletRepository pointWalletRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional(readOnly = true)
    public MyAccountResponse getMyAccount(Long userId) {
        User user = findActiveUser(userId);
        String blogUrl = userBlogRepository.findActiveByUser(user).stream()
                .findFirst()
                .map(UserBlog::getBlogUrl)
                .orElse(null);
        return MyAccountResponse.from(user, blogUrl);
    }

    @Transactional(readOnly = true)
    public MyCampaignSummaryResponse getMyCampaignSummary(Long userId) {
        long recentViewCount = userCampaignRepository
                .findByUserIdAndStatus(userId, UserCampaignStatus.VIEWED)
                .size();
        long likedCount = userCampaignLikeRepository.findByUserId(userId).size();
        List<MyRecentAppliedCampaignSummaryResponse> recentAppliedCampaign = getRecentAppliedCampaignSummaries(userId)
                .stream()
                .limit(RECENT_APPLIED_SUMMARY_COUNT)
                .toList();

        return new MyCampaignSummaryResponse(recentViewCount, likedCount, recentAppliedCampaign);
    }

    @Transactional(readOnly = true)
    public List<MyCampaignListResponse> getMyCampaigns(Long userId, CampaignApplyStatus status) {
        List<UserCampaignApply> userCampaigns = status == null
                ? userCampaignApplyRepository.findByUserId(userId)
                : userCampaignApplyRepository.findByUserIdAndStatus(userId, status);

        Map<Long, Campaign> campaignMap = findCampaignMap(userCampaigns.stream()
                .map(UserCampaignApply::getCampaignId)
                .toList());

        return userCampaigns.stream()
                .map(uc -> {
                    Campaign campaign = campaignMap.get(uc.getCampaignId());
                    String title = campaign != null ? campaign.getTitle() : "삭제된 공고";
                    String brand = campaign != null ? campaign.getBrandName() : "";
                    return MyCampaignListResponse.from(uc, title, brand);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public MyCampaignListResponse getMyCampaignDetail(Long userId, Long userCampaignId) {
        UserCampaignApply uc = userCampaignApplyRepository.findById(userCampaignId)
                .filter(apply -> apply.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(MyErrorCode.USER_CAMPAIGN_NOT_FOUND));
        Campaign campaign = campaignRepository.findActiveById(uc.getCampaignId()).orElse(null);
        String title = campaign != null ? campaign.getTitle() : "삭제된 공고";
        String brand = campaign != null ? campaign.getBrandName() : "";
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

    @Transactional(readOnly = true)
    public List<MyRecentAppliedCampaignListResponse> getRecentApplies(Long userId) {
        List<UserCampaignApply> applies = findRecentApplies(userId);
        Map<Long, Campaign> campaignMap = findCampaignMap(applies.stream()
                .map(UserCampaignApply::getCampaignId)
                .toList());

        return applies.stream()
                .map(apply -> MyRecentAppliedCampaignListResponse.from(apply, campaignMap.get(apply.getCampaignId())))
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
    public MyAiHistoryResponse getAiHistory(Long userId, int size) {
        int safeSize = Math.max(size, 0);
        List<MyAiHistoryItemResponse> histories = blogAnalysisResultRepository.findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .sorted(this::compareCreatedAtDesc)
                .limit(safeSize)
                .map(MyAiHistoryItemResponse::from)
                .toList();
        return new MyAiHistoryResponse(histories);
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

    private List<CampaignSummaryResponse> toCampaignSummaries(List<UserCampaign> userCampaigns) {
        Map<Long, Campaign> map = findCampaignMap(userCampaigns.stream()
                .map(UserCampaign::getCampaignId)
                .toList());
        return userCampaigns.stream()
                .sorted((left, right) -> compareOffsetDateTimeDesc(left.getUpdatedAt(), right.getUpdatedAt()))
                .filter(uc -> map.containsKey(uc.getCampaignId()))
                .map(uc -> CampaignSummaryResponse.from(map.get(uc.getCampaignId())))
                .toList();
    }

    private List<CampaignSummaryResponse> toCampaignSummariesFromLikes(List<UserCampaignLike> likes) {
        Map<Long, Campaign> map = findCampaignMap(likes.stream()
                .map(UserCampaignLike::getCampaignId)
                .toList());
        return likes.stream()
                .sorted((left, right) -> compareOffsetDateTimeDesc(left.getCreatedAt(), right.getCreatedAt()))
                .filter(like -> map.containsKey(like.getCampaignId()))
                .map(like -> CampaignSummaryResponse.from(map.get(like.getCampaignId())))
                .toList();
    }

    private List<MyRecentAppliedCampaignSummaryResponse> getRecentAppliedCampaignSummaries(Long userId) {
        List<UserCampaignApply> applies = findRecentApplies(userId);
        Map<Long, Campaign> campaignMap = findCampaignMap(applies.stream()
                .map(UserCampaignApply::getCampaignId)
                .toList());

        return applies.stream()
                .map(apply -> MyRecentAppliedCampaignSummaryResponse.from(apply, campaignMap.get(apply.getCampaignId())))
                .toList();
    }

    private List<UserCampaignApply> findRecentApplies(Long userId) {
        return userCampaignApplyRepository.findByUserId(userId).stream()
                .sorted(this::compareAppliedAtDesc)
                .toList();
    }

    private int compareAppliedAtDesc(UserCampaignApply left, UserCampaignApply right) {
        return compareOffsetDateTimeDesc(left.getAppliedAt(), right.getAppliedAt());
    }

    private int compareCreatedAtDesc(BlogAnalysisResult left, BlogAnalysisResult right) {
        return compareOffsetDateTimeDesc(left.getCreatedAt(), right.getCreatedAt());
    }

    private int compareOffsetDateTimeDesc(OffsetDateTime left, OffsetDateTime right) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return right.compareTo(left);
    }

    private Map<Long, Campaign> findCampaignMap(List<Long> campaignIds) {
        List<Long> distinctIds = campaignIds.stream().distinct().toList();
        if (distinctIds.isEmpty()) {
            return Map.of();
        }
        return campaignRepository.findAllByIdIn(distinctIds).stream()
                .collect(Collectors.toMap(Campaign::getId, campaign -> campaign));
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }

    private User findActiveUser(Long userId) {
        return userRepository.findActiveById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
