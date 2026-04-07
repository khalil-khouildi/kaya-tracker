package BinDev.ExpensesTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "piggy_bank_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PiggyBankGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Goal name is required")
    @Size(max = 100, message = "Goal name must be less than 100 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "1.0", message = "Target amount must be at least 1")
    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @NotNull(message = "Order index is required")
    @Min(value = 1, message = "Order index must be at least 1")
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Builder.Default
    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}