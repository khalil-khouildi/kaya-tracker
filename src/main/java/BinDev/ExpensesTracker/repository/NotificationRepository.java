package BinDev.ExpensesTracker.repository;

import BinDev.ExpensesTracker.entity.Notification;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Trouver les notifications non lues d'un utilisateur
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    // Trouver toutes les notifications d'un utilisateur
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Trouver les notifications par type
    List<Notification> findByUserAndType(User user, NotificationType type);

    // Marquer une notification comme lue
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :notificationId")
    void markAsRead(@Param("notificationId") Long notificationId, @Param("readAt") LocalDateTime readAt);

    // Marquer toutes les notifications d'un utilisateur comme lues
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.user = :user AND n.isRead = false")
    void markAllAsRead(@Param("user") User user, @Param("readAt") LocalDateTime readAt);

    // Supprimer les anciennes notifications (plus de 30 jours)
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.createdAt < :date")
    void deleteOldNotifications(@Param("date") LocalDateTime date);
}