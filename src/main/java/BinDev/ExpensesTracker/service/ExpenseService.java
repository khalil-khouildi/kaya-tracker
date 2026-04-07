package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.Expense;
import BinDev.ExpensesTracker.entity.ExpenseCategory;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.repository.ExpenseCategoryRepository;
import BinDev.ExpensesTracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final NotificationService notificationService;
    private final StreakService streakService;

    @Transactional
    public Expense addExpense(User user, Long categoryId, String description, BigDecimal amount, String receiptPath) {

        // 1. Récupérer la catégorie
        ExpenseCategory category = expenseCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to this category");
        }

        // 2. Créer et sauvegarder la dépense
        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .description(description)
                .amount(amount)
                .receiptPath(receiptPath)
                .build();

        Expense savedExpense = expenseRepository.save(expense);

        // 3. Mettre à jour le spentAmount
        expenseCategoryRepository.addSpentAmount(categoryId, amount);
        expenseCategoryRepository.flush();

        // 4. Récupérer le spentAmount à jour
        BigDecimal currentSpent = expenseCategoryRepository.getCurrentSpentAmount(categoryId);
        BigDecimal limit = category.getMonthlyLimit();

        System.out.println("=== VÉRIFICATION BUDGET ===");
        System.out.println("Catégorie: " + category.getName().getDisplayName());
        System.out.println("Dépensé: " + currentSpent);
        System.out.println("Limite: " + limit);
        System.out.println("==========================");

        // 5. Vérifier les limites et créer les notifications
        if (currentSpent.compareTo(limit) >= 0) {
            System.out.println("🔔 ALERTE DÉPASSEMENT DÉCLENCHÉE");
            notificationService.createBudgetExceededNotification(user, category);
            streakService.resetOverspendingStreak(user);
        } else {
            BigDecimal ninetyPercent = limit.multiply(new BigDecimal("0.9"));
            if (currentSpent.compareTo(ninetyPercent) >= 0) {
                System.out.println("🔔 ALERTE 90% DÉCLENCHÉE");
                notificationService.createBudgetWarningNotification(user, category);
            }
        }

        // 6. Streak de suivi
        streakService.updateTrackingStreak(user);

        return savedExpense;
    }

    @Transactional
    public void deleteExpense(User user, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        expenseCategoryRepository.subtractSpentAmount(expense.getCategory().getId(), expense.getAmount());
        expenseRepository.delete(expense);
    }

    public List<Expense> getExpensesByMonth(User user, Integer year, Integer month) {
        return expenseRepository.findByUserAndMonth(user, year, month);
    }

    public List<Expense> getRecentExpenses(User user) {
        return expenseRepository.findTop5ByUserOrderByCreatedAtDesc(user);
    }

    public BigDecimal getTotalExpensesByMonth(User user, Integer year, Integer month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
        return expenseRepository.sumByUserAndDateBetween(user, startDate, endDate);
    }
}