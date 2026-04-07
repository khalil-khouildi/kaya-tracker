package BinDev.ExpensesTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false)
    private BigDecimal amount;

    @Size(max = 255, message = "Reason must be less than 255 characters")
    private String reason;

    @Column(name = "borrowed_date", nullable = false)
    private LocalDateTime borrowedDate;

    @Builder.Default
    @Column(name = "is_repaid")
    private Boolean isRepaid = false;

    @Column(name = "expected_repayment_month")
    private Integer expectedRepaymentMonth;

    @Column(name = "expected_repayment_year")
    private Integer expectedRepaymentYear;

    @Column(name = "actual_repayment_date")
    private LocalDate actualRepaymentDate;

    @PrePersist
    protected void onCreate() {
        borrowedDate = LocalDateTime.now();
    }
}