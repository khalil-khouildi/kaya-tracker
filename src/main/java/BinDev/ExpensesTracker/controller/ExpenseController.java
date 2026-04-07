package BinDev.ExpensesTracker.controller;

import BinDev.ExpensesTracker.entity.Expense;
import BinDev.ExpensesTracker.entity.ExpenseCategory;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.service.CategoryService;
import BinDev.ExpensesTracker.service.ExpenseService;
import BinDev.ExpensesTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String listExpenses(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long category,
            Model model) {

        User user = getCurrentUser();

        // Valeurs par défaut : mois et année actuels
        YearMonth now = YearMonth.now();
        int selectedMonth = (month != null) ? month : now.getMonthValue();
        int selectedYear = (year != null) ? year : now.getYear();

        // Récupérer toutes les dépenses du mois
        List<Expense> allExpenses = expenseService.getExpensesByMonth(user, selectedYear, selectedMonth);

        // Appliquer le filtre par catégorie si nécessaire
        List<Expense> filteredExpenses = allExpenses;
        if (category != null && category > 0) {
            filteredExpenses = allExpenses.stream()
                    .filter(exp -> exp.getCategory().getId().equals(category))
                    .collect(Collectors.toList());
        }

        // Calculer le total des dépenses filtrées
        BigDecimal totalExpenses = filteredExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Récupérer toutes les catégories pour le filtre
        List<ExpenseCategory> categories = categoryService.getCurrentMonthCategories(user);

        model.addAttribute("expenses", filteredExpenses);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("currentMonth", selectedMonth);
        model.addAttribute("currentYear", selectedYear);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", categories);
        model.addAttribute("page", "expense/list");
        model.addAttribute("pageTitle", "Mes Dépenses");

        return "layout/base";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        User user = getCurrentUser();

        model.addAttribute("categories", categoryService.getCurrentMonthCategories(user));
        model.addAttribute("page", "expense/form");
        model.addAttribute("userFirstName", user.getFirstName());

        return "layout/base";
    }

    @PostMapping("/add")
    public String addExpense(
            @RequestParam Long categoryId,
            @RequestParam String description,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String receiptPath,
            Model model) {

        try {
            User user = getCurrentUser();
            expenseService.addExpense(user, categoryId, description, amount, receiptPath);
            return "redirect:/expenses?success=true";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.getCurrentMonthCategories(getCurrentUser()));
            model.addAttribute("page", "expense/form");
            return "layout/base";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        User user = getCurrentUser();
        expenseService.deleteExpense(user, id);
        return "redirect:/expenses?deleted=true";
    }
}