package com.ject6.boost.presentation.my.dto;

import com.ject6.boost.domain.my.entity.PointTransaction;
import com.ject6.boost.domain.my.entity.PointWallet;
import java.time.OffsetDateTime;
import java.util.List;

public record PointBalanceResponse(
        int balance,
        List<TransactionItem> transactions
) {
    public record TransactionItem(
            Long id, String type, int amount,
            String description, int balanceAfter, OffsetDateTime createdAt
    ) {
        public static TransactionItem from(PointTransaction t) {
            return new TransactionItem(t.getId(), t.getType().name(),
                    t.getAmount(), t.getDescription(), t.getBalanceAfter(), t.getCreatedAt());
        }
    }

    public static PointBalanceResponse of(PointWallet wallet, List<PointTransaction> transactions) {
        return new PointBalanceResponse(
                wallet.getBalance(),
                transactions.stream().map(TransactionItem::from).toList()
        );
    }
}
