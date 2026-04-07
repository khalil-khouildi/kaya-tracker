package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.*;
import BinDev.ExpensesTracker.enums.CategoryName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final CategoryService categoryService;
    private final PiggyBankService piggyBankService;
    private final NotificationService notificationService;
    private final BorrowService borrowService;
    private final StreakService streakService;
    private final FinancialCalculationService financialCalculationService;

    // Données pour le dashboard
    public DashboardData getDashboardData(User user) {
        YearMonth current = YearMonth.now();
        int year = current.getYear();
        int month = current.getMonthValue();

        DashboardData data = new DashboardData();

        // Totaux du mois
        data.totalIncome = incomeService.getTotalIncomeByMonth(user, year, month);
        data.totalExpenses = expenseService.getTotalExpensesByMonth(user, year, month);
        data.profitLoss = data.totalIncome.subtract(data.totalExpenses);

        // Piggy Bank
        PiggyBank piggyBank = piggyBankService.findByUser(user);
        data.piggyBankBalance = piggyBank.getTotalSavings();

        // Catégories avec leur statut
        List<ExpenseCategory> categories = categoryService.getCurrentMonthCategories(user);
        data.categories = new ArrayList<>();

        for (ExpenseCategory category : categories) {
            CategoryStatus status = new CategoryStatus();
            status.id = category.getId();
            status.name = category.getName();
            status.icon = category.getName().getIcon();
            status.colorCode = category.getName().getColorCode();
            status.monthlyLimit = category.getMonthlyLimit();
            status.spentAmount = category.getSpentAmount();
            status.remainingAmount = category.getMonthlyLimit().subtract(category.getSpentAmount());

            if (category.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0) {
                status.percentageUsed = category.getSpentAmount()
                        .multiply(new BigDecimal("100"))
                        .divide(category.getMonthlyLimit(), 0, RoundingMode.HALF_UP)
                        .intValue();
            } else {
                status.percentageUsed = 0;
            }

            if (category.getSpentAmount().compareTo(category.getMonthlyLimit()) >= 0) {
                status.status = "EXCEEDED";
            } else if (status.percentageUsed >= 90) {
                status.status = "WARNING";
            } else {
                status.status = "OK";
            }

            data.categories.add(status);
        }

        // Dépenses récentes
        data.recentExpenses = expenseService.getRecentExpenses(user);

        // Notifications non lues
        data.unreadNotifications = notificationService.getUnreadNotifications(user);

        // Streaks
        Streak streak = streakService.getStreakByUser(user);
        data.noOverspendingDays = streak.getNoOverspendingDays();
        data.trackingStreak = streak.getTrackingStreak();
        data.bestOverspendingStreak = streak.getBestOverspendingStreak();
        data.bestTrackingStreak = streak.getBestTrackingStreak();

        // Emprunts en cours
        data.pendingBorrowTotal = borrowService.getTotalPendingBorrow(user);

        // Prédiction fin de mois
        data.predictedProfit = financialCalculationService.predictEndOfMonthProfit(user);

        // Objectif actif
        data.activeGoal = piggyBankService.getActiveGoal(user);

        return data;
    }

    // Données pour les graphiques (avec vraies données)
    public ChartData getChartData(User user) {
        YearMonth current = YearMonth.now();
        int year = current.getYear();
        int month = current.getMonthValue();

        ChartData chartData = new ChartData();

        // Initialiser les listes
        chartData.categoryNames = new ArrayList<>();
        chartData.categoryAmounts = new ArrayList<>();
        chartData.days = new ArrayList<>();
        chartData.dailyAmounts = new ArrayList<>();

        // ========== DONNÉES RÉELLES POUR LE CAMEMBERT ==========
        // Récupérer les catégories avec leurs dépenses réelles
        List<ExpenseCategory> categories = categoryService.getCurrentMonthCategories(user);

        for (ExpenseCategory category : categories) {
            BigDecimal spentAmount = category.getSpentAmount();
            if (spentAmount != null && spentAmount.compareTo(BigDecimal.ZERO) > 0) {
                chartData.categoryNames.add(category.getName().getDisplayName());
                chartData.categoryAmounts.add(spentAmount);
            }
        }

        // Si aucune dépense réelle, on laisse les listes vides (pas de données de test)
        // Le graphique ne s'affichera pas mais c'est normal

        // ========== DONNÉES RÉELLES POUR LE GRAPHIQUE LINÉAIRE ==========
        // Récupérer les dépenses quotidiennes réelles
        List<Expense> expenses = expenseService.getExpensesByMonth(user, year, month);
        Map<Integer, BigDecimal> dailyMap = new HashMap<>();

        for (Expense expense : expenses) {
            if (expense.getCreatedAt() != null) {
                int day = expense.getCreatedAt().getDayOfMonth();
                dailyMap.put(day, dailyMap.getOrDefault(day, BigDecimal.ZERO).add(expense.getAmount()));
            }
        }

        // Remplir les 30 jours du mois
        for (int day = 1; day <= current.lengthOfMonth(); day++) {
            chartData.days.add(day);
            BigDecimal amount = dailyMap.getOrDefault(day, BigDecimal.ZERO);
            chartData.dailyAmounts.add(amount);
        }

        // Afficher dans la console IntelliJ
        System.out.println("=== getChartData (DONNÉES RÉELLES) ===");
        System.out.println("CategoryNames: " + chartData.categoryNames);
        System.out.println("CategoryAmounts: " + chartData.categoryAmounts);
        System.out.println("Days size: " + chartData.days.size());
        System.out.println("=====================================");

        return chartData;
    }

    // Classes internes pour les données
    public static class DashboardData {
        public BigDecimal totalIncome;
        public BigDecimal totalExpenses;
        public BigDecimal profitLoss;
        public BigDecimal piggyBankBalance;
        public List<CategoryStatus> categories;
        public List<Expense> recentExpenses;
        public List<Notification> unreadNotifications;
        public int noOverspendingDays;
        public int trackingStreak;
        public int bestOverspendingStreak;
        public int bestTrackingStreak;
        public BigDecimal pendingBorrowTotal;
        public BigDecimal predictedProfit;
        public PiggyBankGoal activeGoal;
    }

    public static class CategoryStatus {
        public Long id;
        public CategoryName name;
        public String icon;
        public String colorCode;
        public BigDecimal monthlyLimit;
        public BigDecimal spentAmount;
        public BigDecimal remainingAmount;
        public Integer percentageUsed;
        public String status;
    }

    public static class ChartData {
        public List<String> categoryNames;
        public List<BigDecimal> categoryAmounts;
        public List<Integer> days;
        public List<BigDecimal> dailyAmounts;
    }
}