package BinDev.ExpensesTracker.repository;

import BinDev.ExpensesTracker.entity.BorrowTransaction;
import BinDev.ExpensesTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, Long> {

    // Trouver tous les emprunts d'un utilisateur
    List<BorrowTransaction> findByUserOrderByBorrowedDateDesc(User user);

    // Trouver les emprunts non remboursés
    List<BorrowTransaction> findByUserAndIsRepaidFalse(User user);

    // Trouver les emprunts à rembourser pour un mois spécifique
    @Query("SELECT b FROM BorrowTransaction b WHERE b.user = :user AND b.isRepaid = false AND b.expectedRepaymentMonth = :month AND b.expectedRepaymentYear = :year")
    List<BorrowTransaction> findPendingRepayments(@Param("user") User user, @Param("month") Integer month, @Param("year") Integer year);

    // Calculer le total des emprunts non remboursés
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM BorrowTransaction b WHERE b.user = :user AND b.isRepaid = false")
    BigDecimal calculateTotalPendingBorrow(@Param("user") User user);

    // Marquer un emprunt comme remboursé
    @Modifying
    @Transactional
    @Query("UPDATE BorrowTransaction b SET b.isRepaid = true, b.actualRepaymentDate = :repaymentDate WHERE b.id = :transactionId")
    void markAsRepaid(@Param("transactionId") Long transactionId, @Param("repaymentDate") LocalDate repaymentDate);
}