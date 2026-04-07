package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.PiggyBank;
import BinDev.ExpensesTracker.entity.PiggyBankGoal;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.repository.PiggyBankGoalRepository;
import BinDev.ExpensesTracker.repository.PiggyBankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PiggyBankService {

    private final PiggyBankRepository piggyBankRepository;
    private final PiggyBankGoalRepository piggyBankGoalRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    @Transactional
    public void createPiggyBankForUser(User user) {
        PiggyBank piggyBank = PiggyBank.builder()
                .user(user)
                .totalSavings(BigDecimal.ZERO)
                .totalBorrowed(BigDecimal.ZERO)
                .totalWithdrawn(BigDecimal.ZERO)
                .build();
        piggyBankRepository.save(piggyBank);
    }

    @Transactional
    public void addToPiggyBank(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        piggyBankRepository.addSavings(user, amount);
    }

    @Transactional
    public boolean withdrawFromPiggyBank(User user, BigDecimal amount) {
        int updated = piggyBankRepository.withdrawSavings(user, amount);
        if (updated > 0) {
            piggyBankRepository.save(findByUser(user));
            return true;
        }
        return false;
    }

    public PiggyBank findByUser(User user) {
        return piggyBankRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("PiggyBank not found"));
    }

    @Transactional
    public void addToTotalBorrowed(User user, BigDecimal amount) {
        piggyBankRepository.addBorrowed(user, amount);
    }

    @Transactional
    public void createDefaultGoalsForUser(User user) {
        Object[][] defaultGoals = {
                {"Nouvel ordinateur", "PC portable pour les études", 1000, 1},
                {"Voyage de fin d'année", "Voyage entre amis", 500, 2},
                {"Fonds d'urgence", "Épargne de sécurité", 2000, 3}
        };

        for (Object[] goal : defaultGoals) {
            PiggyBankGoal piggyBankGoal = PiggyBankGoal.builder()
                    .user(user)
                    .name((String) goal[0])
                    .description((String) goal[1])
                    .targetAmount(new BigDecimal(goal[2].toString()))
                    .orderIndex((Integer) goal[3])
                    .isCompleted(false)
                    .build();
            piggyBankGoalRepository.save(piggyBankGoal);
        }
    }

    public PiggyBankGoal getActiveGoal(User user) {
        return piggyBankGoalRepository.findActiveGoal(user).orElse(null);
    }

    public List<PiggyBankGoal> getAllGoals(User user) {
        return piggyBankGoalRepository.findByUserOrderByOrderIndexAsc(user);
    }

    public List<PiggyBankGoal> getCompletedGoals(User user) {
        return piggyBankGoalRepository.findByUserAndIsCompletedTrueOrderByCompletedDateDesc(user);
    }

    @Transactional
    public void completeGoal(User user, Long goalId) {
        PiggyBankGoal goal = piggyBankGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        if (goal.getIsCompleted()) {
            throw new RuntimeException("Goal already completed");
        }

        PiggyBankGoal activeGoal = getActiveGoal(user);
        if (activeGoal == null || !activeGoal.getId().equals(goalId)) {
            throw new RuntimeException("You must complete goals in order");
        }

        PiggyBank piggyBank = findByUser(user);
        if (piggyBank.getTotalSavings().compareTo(goal.getTargetAmount()) < 0) {
            throw new RuntimeException("Pas assez d'économies pour cet objectif");
        }

        withdrawFromPiggyBank(user, goal.getTargetAmount());

        piggyBank.setTotalWithdrawn(piggyBank.getTotalWithdrawn().add(goal.getTargetAmount()));
        piggyBankRepository.save(piggyBank);

        piggyBankGoalRepository.completeGoal(goalId, LocalDate.now());

        notificationService.createGoalCompletedNotification(user, goal.getName());
        emailService.sendGoalCompletedEmail(user, goal.getName(), goal.getTargetAmount());
    }
}