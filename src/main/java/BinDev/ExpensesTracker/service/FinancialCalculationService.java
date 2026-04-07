package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.*;
import BinDev.ExpensesTracker.enums.StatusType;
import BinDev.ExpensesTracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialCalculationService {

    private final MonthlyStatementRepository monthlyStatementRepository;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final PiggyBankService piggyBankService;
    private final BorrowService borrowService;
    private final NotificationService notificationService;

    // Clôturer le mois et calculer le bilan (appelé par Scheduler)
    @Transactional
    public MonthlyStatement closeMonthlyStatement(User user, Integer year, Integer month) {

        // 1. Calculer les totaux du mois
        BigDecimal totalIncome = incomeService.getTotalIncomeByMonth(user, year, month);
        BigDecimal totalExpenses = expenseService.getTotalExpensesByMonth(user, year, month);
        BigDecimal profitLoss = totalIncome.subtract(totalExpenses);

        // 2. Récupérer le piggy bank avant calcul
        PiggyBank piggyBank = piggyBankService.findByUser(user);
        BigDecimal piggyBankBefore = piggyBank.getTotalSavings();

        // 3. Remboursement des emprunts du mois précédent
        BigDecimal repaymentDeducted = borrowService.processRepaymentsForMonth(user, year, month);

        // 4. Recalculer le profit/perte après remboursement
        BigDecimal netProfitLoss = profitLoss.subtract(repaymentDeducted);

        BigDecimal amountAddedToPiggy = BigDecimal.ZERO;
        BigDecimal borrowedThisMonth = BigDecimal.ZERO;
        BigDecimal remainingBorrowed = BigDecimal.ZERO;
        BigDecimal deficit = BigDecimal.ZERO;
        String status;
        BigDecimal piggyBankAfter;

        if (netProfitLoss.compareTo(BigDecimal.ZERO) >= 0) {
            // SURPLUS : on ajoute au piggy bank
            status = "SURPLUS";
            amountAddedToPiggy = netProfitLoss;
            piggyBankAfter = piggyBankBefore.add(netProfitLoss);
            piggyBankService.addToPiggyBank(user, netProfitLoss);

        } else {
            // DEFICIT : on utilise le piggy bank
            status = "DEFICIT";
            deficit = netProfitLoss.abs();

            if (piggyBankBefore.compareTo(deficit) >= 0) {
                // Le piggy bank couvre le déficit
                amountAddedToPiggy = deficit.negate();
                piggyBankAfter = piggyBankBefore.subtract(deficit);
                piggyBankService.withdrawFromPiggyBank(user, deficit);

            } else {
                // Le piggy bank ne couvre pas tout → emprunt
                amountAddedToPiggy = piggyBankBefore.negate();
                piggyBankAfter = BigDecimal.ZERO;
                piggyBankService.withdrawFromPiggyBank(user, piggyBankBefore);

                borrowedThisMonth = deficit.subtract(piggyBankBefore);
                remainingBorrowed = borrowedThisMonth;

                // Créer l'emprunt
                borrowService.createBorrow(user, borrowedThisMonth, "Déficit du mois de " + month + "/" + year);
            }
        }

        // 5. Créer le bilan mensuel
        MonthlyStatement statement = MonthlyStatement.builder()
                .user(user)
                .year(year)
                .month(month)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .profitLoss(profitLoss)
                .piggyBankBefore(piggyBankBefore)
                .piggyBankAfter(piggyBankAfter)
                .amountAddedToPiggy(amountAddedToPiggy)
                .repaymentDeducted(repaymentDeducted)
                .borrowedThisMonth(borrowedThisMonth)
                .remainingBorrowed(remainingBorrowed)
                .status(StatusType.valueOf(status))
                .isClosed(true)
                .closedDate(LocalDate.now())
                .build();

        MonthlyStatement savedStatement = monthlyStatementRepository.save(statement);

        // 6. Notification
        if ("SURPLUS".equals(status)) {
            notificationService.createMonthlySurplusNotification(user, profitLoss, piggyBankAfter);
        } else {
            notificationService.createMonthlyDeficitNotification(user, deficit, borrowedThisMonth);
        }

        return savedStatement;
    }

    // Prédire le profit en fin de mois
    public BigDecimal predictEndOfMonthProfit(User user) {
        YearMonth current = YearMonth.now();
        int daysPassed = LocalDate.now().getDayOfMonth();
        int totalDays = current.lengthOfMonth();

        BigDecimal currentExpenses = expenseService.getTotalExpensesByMonth(
                user, current.getYear(), current.getMonthValue());
        BigDecimal totalIncome = incomeService.getTotalIncomeByMonth(
                user, current.getYear(), current.getMonthValue());

        if (daysPassed == 0 || currentExpenses.compareTo(BigDecimal.ZERO) == 0) {
            return totalIncome;
        }

        BigDecimal avgDailyExpense = currentExpenses.divide(
                BigDecimal.valueOf(daysPassed), 2, RoundingMode.HALF_UP);
        BigDecimal projectedExpenses = avgDailyExpense.multiply(BigDecimal.valueOf(totalDays));
        BigDecimal predictedProfit = totalIncome.subtract(projectedExpenses);

        return predictedProfit;
    }
}