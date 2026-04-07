package BinDev.ExpensesTracker.entity;

import BinDev.ExpensesTracker.enums.CategoryName;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "expense_categories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name", "month", "year"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Category name is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryName name;

    @NotNull(message = "Monthly limit is required")
    @DecimalMin(value = "0.0", message = "Monthly limit must be positive")
    @Column(name = "monthly_limit", nullable = false)
    @Builder.Default
    private BigDecimal monthlyLimit = new BigDecimal("50");

    @Builder.Default
    @Column(name = "spent_amount")
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Builder.Default
    @Column(name = "is_modified")
    private Boolean isModified = false;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        YearMonth now = YearMonth.now();
        this.month = now.getMonthValue();
        this.year = now.getYear();
    }
}