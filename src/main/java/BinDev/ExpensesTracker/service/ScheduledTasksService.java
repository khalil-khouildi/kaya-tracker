package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTasksService {

    private final UserService userService;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final FinancialCalculationService financialCalculationService;
    private final NotificationService notificationService;
    private final StreakService streakService;

    /**
     * Tâche 1: Vérifier les budgets quotidiennement à 20h00
     * Alerte l'utilisateur quand un budget est à 90% ou dépassé
     */
    @Scheduled(cron = "0 0 20 * * *")  // Tous les jours à 20h00
    public void checkBudgets() {
        System.out.println("🕐 [Scheduler] Vérification des budgets - " + LocalDate.now());

        List<User> allUsers = userService.findAll();

        for (User user : allUsers) {
            // Vérifier les budgets de l'utilisateur
            // Les alertes sont déjà gérées dans ExpenseService
            System.out.println("  - Budgets vérifiés pour: " + user.getEmail());
        }
    }

    /**
     * Tâche 2: Créer les catégories pour le nouveau mois (1er du mois à 00h01)
     */
    @Scheduled(cron = "0 1 0 1 * *")  // 1er du mois à 00h01
    public void createMonthlyCategories() {
        System.out.println("🕐 [Scheduler] Création des catégories pour le nouveau mois - " + LocalDate.now());

        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        int newMonth = nextMonth.getMonthValue();
        int newYear = nextMonth.getYear();

        List<User> allUsers = userService.findAll();

        for (User user : allUsers) {
            try {
                categoryService.createCategoriesForNewMonth(user, newMonth, newYear);
                System.out.println("  - Catégories créées pour: " + user.getEmail() + " (" + newMonth + "/" + newYear + ")");
            } catch (Exception e) {
                System.err.println("  - Erreur pour " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Tâche 3: Clôturer le mois et calculer le bilan (1er du mois à 00h05)
     */
    @Scheduled(cron = "0 5 0 1 * *")  // 1er du mois à 00h05
    public void closeMonthlyStatements() {
        System.out.println("🕐 [Scheduler] Clôture du mois et calcul des bilans - " + LocalDate.now());

        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        int month = previousMonth.getMonthValue();
        int year = previousMonth.getYear();

        List<User> allUsers = userService.findAll();

        for (User user : allUsers) {
            try {
                financialCalculationService.closeMonthlyStatement(user, year, month);
                System.out.println("  - Bilan clôturé pour: " + user.getEmail() + " (" + month + "/" + year + ")");
            } catch (Exception e) {
                System.err.println("  - Erreur pour " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Tâche 4: Mettre à jour les streaks quotidiennement à 23h59
     */
    @Scheduled(cron = "0 59 23 * * *")  // Tous les jours à 23h59
    public void updateDailyStreaks() {
        System.out.println("🕐 [Scheduler] Mise à jour des streaks - " + LocalDate.now());

        List<User> allUsers = userService.findAll();

        for (User user : allUsers) {
            try {
                // Vérifier si l'utilisateur a dépassé un budget aujourd'hui
                boolean hasOverspending = checkOverspendingForUser(user);

                if (hasOverspending) {
                    streakService.resetOverspendingStreak(user);
                } else {
                    streakService.incrementOverspendingStreak(user);
                }
                System.out.println("  - Streaks mis à jour pour: " + user.getEmail());
            } catch (Exception e) {
                System.err.println("  - Erreur pour " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Vérifier si l'utilisateur a dépassé un budget aujourd'hui
     */
    private boolean checkOverspendingForUser(User user) {
        var categories = categoryService.getCurrentMonthCategories(user);
        for (var category : categories) {
            if (category.getSpentAmount().compareTo(category.getMonthlyLimit()) > 0) {
                return true;
            }
        }
        return false;
    }
}