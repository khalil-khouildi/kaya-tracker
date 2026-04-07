package BinDev.ExpensesTracker.repository;

import BinDev.ExpensesTracker.entity.PiggyBankGoal;
import BinDev.ExpensesTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PiggyBankGoalRepository extends JpaRepository<PiggyBankGoal, Long> {

    // Trouver tous les objectifs d'un utilisateur (non complétés d'abord)
    List<PiggyBankGoal> findByUserOrderByOrderIndexAsc(User user);

    // Trouver l'objectif actif (premier non complété)
    @Query("SELECT g FROM PiggyBankGoal g WHERE g.user = :user AND g.isCompleted = false ORDER BY g.orderIndex ASC LIMIT 1")
    Optional<PiggyBankGoal> findActiveGoal(@Param("user") User user);

    // Trouver tous les objectifs complétés
    List<PiggyBankGoal> findByUserAndIsCompletedTrueOrderByCompletedDateDesc(User user);

    // Marquer un objectif comme complété
    @Modifying
    @Transactional
    @Query("UPDATE PiggyBankGoal g SET g.isCompleted = true, g.completedDate = :completedDate WHERE g.id = :goalId")
    void completeGoal(@Param("goalId") Long goalId, @Param("completedDate") LocalDate completedDate);
}