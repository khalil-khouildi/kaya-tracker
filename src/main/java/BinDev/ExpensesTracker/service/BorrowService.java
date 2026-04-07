package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.BorrowTransaction;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.repository.BorrowTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowTransactionRepository borrowTransactionRepository;
    private final PiggyBankService piggyBankService;

    // Créer un emprunt (appelé en fin de mois)
    @Transactional
    public BorrowTransaction createBorrow(User user, BigDecimal amount, String reason) {

        BorrowTransaction borrow = BorrowTransaction.builder()
                .user(user)
                .amount(amount)
                .reason(reason)
                .isRepaid(false)
                .expectedRepaymentMonth(LocalDate.now().getMonthValue() + 1)
                .expectedRepaymentYear(LocalDate.now().getYear())
                .build();

        if (borrow.getExpectedRepaymentMonth() == 13) {
            borrow.setExpectedRepaymentMonth(1);
            borrow.setExpectedRepaymentYear(borrow.getExpectedRepaymentYear() + 1);
        }

        BorrowTransaction savedBorrow = borrowTransactionRepository.save(borrow);

        // Mettre à jour le total emprunté dans piggy bank
        piggyBankService.addToTotalBorrowed(user, amount);

        return savedBorrow;
    }

    // Traiter les remboursements du mois (appelé par Scheduler)
    @Transactional
    public BigDecimal processRepaymentsForMonth(User user, Integer year, Integer month) {
        List<BorrowTransaction> pendingRepayments = borrowTransactionRepository
                .findPendingRepayments(user, month, year);

        BigDecimal totalRepaid = BigDecimal.ZERO;

        for (BorrowTransaction borrow : pendingRepayments) {
            totalRepaid = totalRepaid.add(borrow.getAmount());
            borrowTransactionRepository.markAsRepaid(borrow.getId(), LocalDate.now());
        }

        return totalRepaid;
    }

    // Calculer le montant maximum empruntable
    public BigDecimal calculateMaxBorrowAmount(User user) {
        // Basé sur la moyenne des 3 derniers mois
        // À implémenter avec les données réelles
        return new BigDecimal("500"); // Valeur par défaut
    }

    // Récupérer tous les emprunts d'un utilisateur
    public List<BorrowTransaction> getUserBorrows(User user) {
        return borrowTransactionRepository.findByUserOrderByBorrowedDateDesc(user);
    }

    // Récupérer les emprunts non remboursés
    public List<BorrowTransaction> getPendingBorrows(User user) {
        return borrowTransactionRepository.findByUserAndIsRepaidFalse(user);
    }

    // Calculer le total des emprunts non remboursés
    public BigDecimal getTotalPendingBorrow(User user) {
        return borrowTransactionRepository.calculateTotalPendingBorrow(user);
    }
}