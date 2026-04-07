package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.Income;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;

    // Ajouter un revenu
    @Transactional
    public Income addIncome(User user, String source, BigDecimal amount, Boolean isRecurring) {
        Income income = Income.builder()
                .user(user)
                .source(source)
                .amount(amount)
                .isRecurring(isRecurring != null && isRecurring)
                .build();

        return incomeRepository.save(income);
    }

    // Supprimer un revenu
    @Transactional
    public void deleteIncome(User user, Long incomeId) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        if (!income.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        incomeRepository.delete(income);
    }

    // Récupérer les revenus du mois
    public List<Income> getIncomesByMonth(User user, Integer year, Integer month) {
        return incomeRepository.findByUserAndMonth(user, year, month);
    }

    // Calculer le total des revenus du mois
    public BigDecimal getTotalIncomeByMonth(User user, Integer year, Integer month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
        return incomeRepository.sumByUserAndDateBetween(user, startDate, endDate);
    }

    // Récupérer tous les revenus d'un utilisateur
    public List<Income> getAllIncomes(User user) {
        return incomeRepository.findByUserOrderByCreatedAtDesc(user);
    }
}