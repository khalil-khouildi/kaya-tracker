package BinDev.ExpensesTracker.repository;

import BinDev.ExpensesTracker.entity.MonthlyStatement;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.enums.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyStatementRepository extends JpaRepository<MonthlyStatement, Long> {

    // Trouver le bilan d'un utilisateur pour un mois spécifique
    Optional<MonthlyStatement> findByUserAndYearAndMonth(User user, Integer year, Integer month);

    // Trouver tous les bilans d'un utilisateur (triés par date décroissante)
    List<MonthlyStatement> findByUserOrderByYearDescMonthDesc(User user);

    // Trouver le dernier bilan d'un utilisateur
    @Query("SELECT m FROM MonthlyStatement m WHERE m.user = :user ORDER BY m.year DESC, m.month DESC LIMIT 1")
    Optional<MonthlyStatement> findLastStatement(@Param("user") User user);

    // Vérifier si un bilan existe déjà pour ce mois
    boolean existsByUserAndYearAndMonth(User user, Integer year, Integer month);

    // Trouver les bilans par statut
    List<MonthlyStatement> findByUserAndStatus(User user, StatusType status);
}