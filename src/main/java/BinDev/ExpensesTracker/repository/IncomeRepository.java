package BinDev.ExpensesTracker.repository;

import BinDev.ExpensesTracker.entity.Income;
import BinDev.ExpensesTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    // Trouver tous les revenus d'un utilisateur
    List<Income> findByUserOrderByCreatedAtDesc(User user);

    // Trouver les revenus d'un utilisateur pour un mois spécifique
    @Query("SELECT i FROM Income i WHERE i.user = :user AND YEAR(i.createdAt) = :year AND MONTH(i.createdAt) = :month ORDER BY i.createdAt DESC")
    List<Income> findByUserAndMonth(@Param("user") User user, @Param("year") Integer year, @Param("month") Integer month);

    // Calculer le total des revenus pour un utilisateur sur une période
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user = :user AND i.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserAndDateBetween(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Trouver les revenus récurrents
    List<Income> findByUserAndIsRecurringTrue(User user);
}