package BinDev.ExpensesTracker.repository;

import BinDev.ExpensesTracker.entity.Expense;
import BinDev.ExpensesTracker.entity.ExpenseCategory;
import BinDev.ExpensesTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Trouver toutes les dépenses d'un utilisateur
    List<Expense> findByUserOrderByCreatedAtDesc(User user);

    // Trouver les dépenses par catégorie
    List<Expense> findByCategoryOrderByCreatedAtDesc(ExpenseCategory category);

    // Trouver les dépenses d'un utilisateur pour un mois spécifique
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND YEAR(e.createdAt) = :year AND MONTH(e.createdAt) = :month ORDER BY e.createdAt DESC")
    List<Expense> findByUserAndMonth(@Param("user") User user, @Param("year") Integer year, @Param("month") Integer month);

    // Trouver les dépenses d'une catégorie pour un mois spécifique
    @Query("SELECT e FROM Expense e WHERE e.category = :category AND YEAR(e.createdAt) = :year AND MONTH(e.createdAt) = :month ORDER BY e.createdAt DESC")
    List<Expense> findByCategoryAndMonth(@Param("category") ExpenseCategory category, @Param("year") Integer year, @Param("month") Integer month);

    // Calculer le total des dépenses pour un utilisateur sur une période
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserAndDateBetween(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Trouver les dernières dépenses (limité)
    List<Expense> findTop5ByUserOrderByCreatedAtDesc(User user);


}