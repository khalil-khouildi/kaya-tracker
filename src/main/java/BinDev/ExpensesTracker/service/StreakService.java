package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.Streak;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.enums.NotificationType;
import BinDev.ExpensesTracker.repository.StreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final StreakRepository streakRepository;
    private final NotificationService notificationService;

    // Créer une streak pour un nouvel utilisateur
    @Transactional
    public void createStreakForUser(User user) {
        Streak streak = Streak.builder()
                .user(user)
                .noOverspendingDays(0)
                .bestOverspendingStreak(0)
                .trackingStreak(0)
                .bestTrackingStreak(0)
                .build();

        streakRepository.save(streak);
    }

    // Mettre à jour le streak de suivi (quand une dépense est ajoutée)
    @Transactional
    public void updateTrackingStreak(User user) {
        Streak streak = getStreakByUser(user);
        LocalDate today = LocalDate.now();

        if (streak.getLastTrackingDate() == null || !streak.getLastTrackingDate().equals(today)) {
            streakRepository.incrementTrackingStreak(user);
            streakRepository.updateLastTrackingDate(user, today);

            int currentStreak = streak.getTrackingStreak() + 1;
            if (currentStreak > streak.getBestTrackingStreak()) {
                streakRepository.updateBestTrackingStreak(user, currentStreak);

                if (currentStreak == 7 || currentStreak == 14 || currentStreak == 30) {
                    notificationService.createNotification(user, NotificationType.STREAK_MILESTONE);
                }
            }
        }
    }

    // Réinitialiser le streak de non-dépassement
    @Transactional
    public void resetOverspendingStreak(User user) {
        Streak streak = getStreakByUser(user);

        if (streak.getNoOverspendingDays() > streak.getBestOverspendingStreak()) {
            streakRepository.updateBestOverspendingStreak(user, streak.getNoOverspendingDays());
        }

        streakRepository.resetOverspendingStreak(user, LocalDate.now());
    }

    // Incrémenter le streak de non-dépassement
    @Transactional
    public void incrementOverspendingStreak(User user) {
        streakRepository.incrementOverspendingStreak(user);
    }

    // Récupérer la streak d'un utilisateur
    public Streak getStreakByUser(User user) {
        return streakRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Streak not found"));
    }



}