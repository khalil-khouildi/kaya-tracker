package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.ExpenseCategory;
import BinDev.ExpensesTracker.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Bienvenue sur Student Budget Tracker !");
            message.setText("Bonjour " + user.getFirstName() + ",\n\n" +
                    "Bienvenue sur Student Budget Tracker ! 🎉\n\n" +
                    "Connectez-vous : http://localhost:8080/login\n\n" +
                    "À bientôt !");
            mailSender.send(message);
            System.out.println("✅ Email de bienvenue envoyé à " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
        }
    }

    public void sendBudgetWarningEmail(User user, ExpenseCategory category) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("⚠️ Alerte budget : " + category.getName().getDisplayName());
            message.setText("Bonjour " + user.getFirstName() + ",\n\n" +
                    "⚠️ Vous avez atteint 90% de votre budget pour " + category.getName().getDisplayName() + " !\n\n" +
                    "Connectez-vous : http://localhost:8080/dashboard");
            mailSender.send(message);
            System.out.println("✅ Email alerte budget envoyé à " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
        }
    }

    public void sendBudgetExceededEmail(User user, ExpenseCategory category) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("❌ Budget dépassé : " + category.getName().getDisplayName());
            message.setText("Bonjour " + user.getFirstName() + ",\n\n" +
                    "❌ Vous avez dépassé votre budget pour " + category.getName().getDisplayName() + " !\n\n" +
                    "Connectez-vous : http://localhost:8080/dashboard");
            mailSender.send(message);
            System.out.println("✅ Email dépassement budget envoyé à " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
        }
    }

    public void sendGoalCompletedEmail(User user, String goalName, BigDecimal targetAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("🎉 Objectif atteint : " + goalName);
            message.setText("Bonjour " + user.getFirstName() + ",\n\n" +
                    "🎉 FÉLICITATIONS ! Vous avez atteint votre objectif : " + goalName + "\n\n" +
                    "Connectez-vous : http://localhost:8080/piggybank");
            mailSender.send(message);
            System.out.println("✅ Email objectif atteint envoyé à " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
        }
    }
}