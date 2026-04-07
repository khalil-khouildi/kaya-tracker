package BinDev.ExpensesTracker.enums;

public enum NotificationType {
    BUDGET_WARNING("⚠️ Attention: budget bientôt atteint"),
    BUDGET_EXCEEDED("❌ Budget dépassé"),
    MONTHLY_DEFICIT("📉 Mois en déficit"),
    MONTHLY_SURPLUS("✅ Mois en surplus"),
    BORROW_CREATED("💰 Emprunt enregistré"),
    GOAL_COMPLETED("🎉 Objectif atteint !"),
    WEEKLY_SUMMARY("📊 Résumé hebdomadaire"),
    STREAK_MILESTONE("🔥 Nouveau record !");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}