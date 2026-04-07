package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.ExpenseCategory;
import BinDev.ExpensesTracker.entity.Notification;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.enums.NotificationType;
import BinDev.ExpensesTracker.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;  // ← AJOUTER CETTE LIGNE

    @Transactional
    public void createNotification(User user, NotificationType type) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    public void createBudgetWarningNotification(User user, ExpenseCategory category) {
        System.out.println("📢 CRÉATION NOTIFICATION BUDGET WARNING pour " + user.getEmail());
        createNotification(user, NotificationType.BUDGET_WARNING);
        emailService.sendBudgetWarningEmail(user, category);
    }

    public void createBudgetExceededNotification(User user, ExpenseCategory category) {
        System.out.println("📢 CRÉATION NOTIFICATION BUDGET EXCEEDED pour " + user.getEmail());
        createNotification(user, NotificationType.BUDGET_EXCEEDED);
        emailService.sendBudgetExceededEmail(user, category);
    }

    public void createMonthlySurplusNotification(User user, BigDecimal profit, BigDecimal newBalance) {
        createNotification(user, NotificationType.MONTHLY_SURPLUS);
    }

    public void createMonthlyDeficitNotification(User user, BigDecimal deficit, BigDecimal borrowed) {
        createNotification(user, NotificationType.MONTHLY_DEFICIT);
    }

    public void createGoalCompletedNotification(User user, String goalName) {
        System.out.println("📢 CRÉATION NOTIFICATION GOAL COMPLETED pour " + user.getEmail());
        createNotification(user, NotificationType.GOAL_COMPLETED);
        emailService.sendGoalCompletedEmail(user, goalName, BigDecimal.ZERO);
    }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId, java.time.LocalDateTime.now());
    }

    @Transactional
    public void markAllAsRead(User user) {
        System.out.println("📢 Marquer toutes les notifications comme lues pour " + user.getEmail());
        notificationRepository.markAllAsRead(user, java.time.LocalDateTime.now());
    }
}