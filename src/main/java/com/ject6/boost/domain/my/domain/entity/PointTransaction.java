package com.ject6.boost.domain.my.domain.entity;

import com.ject6.boost.domain.my.domain.constant.PointTransactionType;
import com.ject6.boost.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "point_transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PointTransactionType type;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "description")
    private String description;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_holder")
    private String accountHolder;

    @Column(name = "status", length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public static PointTransaction ofWithdraw(User user, int amount, int balanceAfter,
                                               String bankName, String accountNumber, String accountHolder) {
        PointTransaction t = new PointTransaction();
        t.user = user;
        t.type = PointTransactionType.WITHDRAW;
        t.amount = amount;
        t.balanceAfter = balanceAfter;
        t.description = "포인트 출금 신청";
        t.bankName = bankName;
        t.accountNumber = accountNumber;
        t.accountHolder = accountHolder;
        t.status = "PENDING";
        return t;
    }
}
