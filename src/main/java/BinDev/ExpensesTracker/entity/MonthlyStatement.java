package BinDev.ExpensesTracker.entity;

import BinDev.ExpensesTracker.enums.StatusType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "monthly_statements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year", "month"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Builder.Default
    @Column(name = "total_income")
    private BigDecimal totalIncome = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_expenses")
    private BigDecimal totalExpenses = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "profit_loss")
    private BigDecimal profitLoss = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "piggy_bank_before")
    private BigDecimal piggyBankBefore = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "piggy_bank_after")
    private BigDecimal piggyBankAfter = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "amount_added_to_piggy")
    private BigDecimal amountAddedToPiggy = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "repayment_deducted")
    private BigDecimal repaymentDeducted = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "borrowed_this_month")
    private BigDecimal borrowedThisMonth = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "remaining_borrowed")
    private BigDecimal remainingBorrowed = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusType status;  // ← Changé de String à Enum

    @Builder.Default
    @Column(name = "is_closed")
    private Boolean isClosed = false;

    @Column(name = "closed_date")
    private LocalDate closedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        YearMonth now = YearMonth.now();
        this.year = now.getYear();
        this.month = now.getMonthValue();
    }
}