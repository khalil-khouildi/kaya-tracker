package BinDev.ExpensesTracker.controller;

import BinDev.ExpensesTracker.entity.Income;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.service.IncomeService;
import BinDev.ExpensesTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;
    private final UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String listIncomes(Model model) {
        User user = getCurrentUser();
        YearMonth now = YearMonth.now();

        List<Income> incomes = incomeService.getIncomesByMonth(user, now.getYear(), now.getMonthValue());
        BigDecimal totalIncome = incomeService.getTotalIncomeByMonth(user, now.getYear(), now.getMonthValue());

        model.addAttribute("incomes", incomes);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("currentMonth", now.getMonthValue());
        model.addAttribute("currentYear", now.getYear());
        model.addAttribute("page", "income/list");

        return "layout/base";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("page", "income/form");
        return "layout/base";
    }

    @PostMapping("/add")
    public String addIncome(
            @RequestParam String source,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) Boolean isRecurring,
            Model model) {

        try {
            User user = getCurrentUser();
            incomeService.addIncome(user, source, amount, isRecurring);
            return "redirect:/incomes?success=true";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("page", "income/form");
            return "layout/base";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteIncome(@PathVariable Long id) {
        User user = getCurrentUser();
        incomeService.deleteIncome(user, id);
        return "redirect:/incomes?deleted=true";
    }
}