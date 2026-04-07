package BinDev.ExpensesTracker.enums;

public enum CategoryName {
    FOOD("Nourriture", "fas fa-utensils", "#28a745"),
    TRANSPORT("Transport", "fas fa-bus", "#007bff"),
    STUDY("Études", "fas fa-graduation-cap", "#fd7e14"),
    BOOKS("Livres/Cours", "fas fa-book", "#17a2b8"),
    ENTERTAINMENT("Divertissement", "fas fa-film", "#dc3545"),
    RENT("Loyer", "fas fa-home", "#6f42c1"),
    SHOPPING("Shopping", "fas fa-shopping-bag", "#e83e8c"),
    HEALTH("Santé", "fas fa-heartbeat", "#20c997"),
    OTHER("Autres", "fas fa-ellipsis-h", "#6c757d");

    private final String displayName;
    private final String icon;
    private final String colorCode;

    CategoryName(String displayName, String icon, String colorCode) {
        this.displayName = displayName;
        this.icon = icon;
        this.colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public String getColorCode() {
        return colorCode;
    }
}