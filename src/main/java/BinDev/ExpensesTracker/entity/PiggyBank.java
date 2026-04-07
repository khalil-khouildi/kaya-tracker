package BinDev.ExpensesTracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "piggy_banks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PiggyBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(name = "total_savings")
    private BigDecimal totalSavings = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_borrowed")
    private BigDecimal totalBorrowed = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_withdrawn")
    private BigDecimal totalWithdrawn = BigDecimal.ZERO;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}