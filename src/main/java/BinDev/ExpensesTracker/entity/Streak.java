package BinDev.ExpensesTracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "streaks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Streak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(name = "no_overspending_days")
    private Integer noOverspendingDays = 0;

    @Builder.Default
    @Column(name = "best_overspending_streak")
    private Integer bestOverspendingStreak = 0;

    @Builder.Default
    @Column(name = "tracking_streak")
    private Integer trackingStreak = 0;

    @Builder.Default
    @Column(name = "best_tracking_streak")
    private Integer bestTrackingStreak = 0;

    @Column(name = "last_expense_date")
    private LocalDate lastExpenseDate;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;

    @Column(name = "last_tracking_date")
    private LocalDate lastTrackingDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}