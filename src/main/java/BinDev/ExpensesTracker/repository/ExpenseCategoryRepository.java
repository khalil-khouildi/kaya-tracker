package BinDev.ExpensesTracker.repository;

import BinDev.ExpensesTracker.entity.ExpenseCategory;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.enums.CategoryName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    // Trouver les catégories d'un utilisateur pour un mois spécifique
    List<ExpenseCategory> findByUserAndMonthAndYear(User user, Integer month, Integer year);

    // Trouver une catégorie spécifique d'un utilisateur pour un mois
    Optional<ExpenseCategory> findByUserAndNameAndMonthAndYear(
            User user, CategoryName name, Integer month, Integer year);

    // Vérifier si une catégorie existe déjà pour ce mois
    boolean existsByUserAndNameAndMonthAndYear(
            User user, CategoryName name, Integer month, Integer year);

    // Mettre à jour le montant dépensé
    @Modifying
    @Transactional
    @Query("UPDATE ExpenseCategory c SET c.spentAmount = c.spentAmount + :amount WHERE c.id = :categoryId")
    void addSpentAmount(@Param("categoryId") Long categoryId, @Param("amount") BigDecimal amount);

    @Modifying
    @Transactional
    @Query("UPDATE ExpenseCategory c SET c.spentAmount = c.spentAmount - :amount WHERE c.id = :categoryId")
    void subtractSpentAmount(@Param("categoryId") Long categoryId, @Param("amount") BigDecimal amount);

    // Mettre à jour la limite mensuelle
    @Modifying
    @Transactional
    @Query("UPDATE ExpenseCategory c SET c.monthlyLimit = :newLimit, c.isModified = true WHERE c.id = :categoryId")
    void updateMonthlyLimit(@Param("categoryId") Long categoryId, @Param("newLimit") BigDecimal newLimit);

    @Query("SELECT c.spentAmount FROM ExpenseCategory c WHERE c.id = :categoryId")
    BigDecimal getCurrentSpentAmount(@Param("categoryId") Long categoryId);
}