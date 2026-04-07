package BinDev.ExpensesTracker.repository;

import BinDev.ExpensesTracker.entity.PiggyBank;
import BinDev.ExpensesTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PiggyBankRepository extends JpaRepository<PiggyBank, Long> {

    // Trouver la tirelire d'un utilisateur
    Optional<PiggyBank> findByUser(User user);

    // Ajouter de l'argent à la tirelire
    @Modifying
    @Transactional
    @Query("UPDATE PiggyBank p SET p.totalSavings = p.totalSavings + :amount WHERE p.user = :user")
    void addSavings(@Param("user") User user, @Param("amount") BigDecimal amount);

    // Retirer de l'argent de la tirelire
    @Modifying
    @Transactional
    @Query("UPDATE PiggyBank p SET p.totalSavings = p.totalSavings - :amount WHERE p.user = :user AND p.totalSavings >= :amount")
    int withdrawSavings(@Param("user") User user, @Param("amount") BigDecimal amount);

    // Ajouter au total emprunté
    @Modifying
    @Transactional
    @Query("UPDATE PiggyBank p SET p.totalBorrowed = p.totalBorrowed + :amount WHERE p.user = :user")
    void addBorrowed(@Param("user") User user, @Param("amount") BigDecimal amount);
}