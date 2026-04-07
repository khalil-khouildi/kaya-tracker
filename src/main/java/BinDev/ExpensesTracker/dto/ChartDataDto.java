package BinDev.ExpensesTracker.dto;

import java.math.BigDecimal;
import java.util.List;

public class ChartDataDto {
    private List<String> categoryNames;
    private List<BigDecimal> categoryAmounts;
    private List<Integer> days;
    private List<BigDecimal> dailyAmounts;

    // Constructeurs
    public ChartDataDto() {}

    public ChartDataDto(List<String> categoryNames, List<BigDecimal> categoryAmounts,
                        List<Integer> days, List<BigDecimal> dailyAmounts) {
        this.categoryNames = categoryNames;
        this.categoryAmounts = categoryAmounts;
        this.days = days;
        this.dailyAmounts = dailyAmounts;
    }

    // Getters et Setters
    public List<String> getCategoryNames() { return categoryNames; }
    public void setCategoryNames(List<String> categoryNames) { this.categoryNames = categoryNames; }
    public List<BigDecimal> getCategoryAmounts() { return categoryAmounts; }
    public void setCategoryAmounts(List<BigDecimal> categoryAmounts) { this.categoryAmounts = categoryAmounts; }
    public List<Integer> getDays() { return days; }
    public void setDays(List<Integer> days) { this.days = days; }
    public List<BigDecimal> getDailyAmounts() { return dailyAmounts; }
    public void setDailyAmounts(List<BigDecimal> dailyAmounts) { this.dailyAmounts = dailyAmounts; }
}