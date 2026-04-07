package BinDev.ExpensesTracker.repository;

import BinDev.ExpensesTracker.entity.Streak;
import BinDev.ExpensesTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StreakRepository extends JpaRepository<Streak, Long> {

    // Trouver la streak d'un utilisateur
    Optional<Streak> findByUser(User user);

    // Incrémenter le compteur de jours sans dépassement
    @Modifying
    @Transactional
    @Query("UPDATE Streak s SET s.noOverspendingDays = s.noOverspendingDays + 1 WHERE s.user = :user")
    void incrementOverspendingStreak(@Param("user") User user);

    // Réinitialiser le compteur de jours sans dépassement
    @Modifying
    @Transactional
    @Query("UPDATE Streak s SET s.noOverspendingDays = 0, s.lastResetDate = :resetDate WHERE s.user = :user")
    void resetOverspendingStreak(@Param("user") User user, @Param("resetDate") LocalDate resetDate);

    // Mettre à jour le record si nécessaire
    @Modifying
    @Transactional
    @Query("UPDATE Streak s SET s.bestOverspendingStreak = :newRecord WHERE s.user = :user AND s.bestOverspendingStreak < :newRecord")
    void updateBestOverspendingStreak(@Param("user") User user, @Param("newRecord") Integer newRecord);

    // Incrémenter le compteur de suivi
    @Modifying
    @Transactional
    @Query("UPDATE Streak s SET s.trackingStreak = s.trackingStreak + 1 WHERE s.user = :user")
    void incrementTrackingStreak(@Param("user") User user);

    // Réinitialiser le compteur de suivi
    @Modifying
    @Transactional
    @Query("UPDATE Streak s SET s.trackingStreak = 0 WHERE s.user = :user")
    void resetTrackingStreak(@Param("user") User user);

    // Mettre à jour le record de suivi si nécessaire
    @Modifying
    @Transactional
    @Query("UPDATE Streak s SET s.bestTrackingStreak = :newRecord WHERE s.user = :user AND s.bestTrackingStreak < :newRecord")
    void updateBestTrackingStreak(@Param("user") User user, @Param("newRecord") Integer newRecord);

    // Mettre à jour la dernière date de dépense
    @Modifying
    @Transactional
    @Query("UPDATE Streak s SET s.lastExpenseDate = :expenseDate WHERE s.user = :user")
    void updateLastExpenseDate(@Param("user") User user, @Param("expenseDate") LocalDate expenseDate);

    // Mettre à jour la dernière date de suivi
    @Modifying
    @Transactional
    @Query("UPDATE Streak s SET s.lastTrackingDate = :trackingDate WHERE s.user = :user")
    void updateLastTrackingDate(@Param("user") User user, @Param("trackingDate") LocalDate trackingDate);
}