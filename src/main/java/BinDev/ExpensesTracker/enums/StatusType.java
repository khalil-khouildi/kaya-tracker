package BinDev.ExpensesTracker.enums;

public enum StatusType {
    SURPLUS("Surplus"),
    DEFICIT("Déficit"),
    BALANCED("Équilibré");

    private final String displayName;

    StatusType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}