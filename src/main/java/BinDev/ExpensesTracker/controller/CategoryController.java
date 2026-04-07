package BinDev.ExpensesTracker.controller;

import BinDev.ExpensesTracker.entity.ExpenseCategory;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.service.CategoryService;
import BinDev.ExpensesTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String listCategories(Model model) {
        User user = getCurrentUser();

        List<ExpenseCategory> categories = categoryService.getCurrentMonthCategories(user);

        model.addAttribute("categories", categories);
        model.addAttribute("page", "category/list");

        return "layout/base";
    }

    @PostMapping("/modify/{id}")
    public String modifyLimit(
            @PathVariable Long id,
            @RequestParam BigDecimal newLimit,
            Model model) {

        try {
            User user = getCurrentUser();
            categoryService.modifyMonthlyLimit(user, id, newLimit);
            return "redirect:/categories?success=true";

        } catch (RuntimeException e) {
            return "redirect:/categories?error=" + e.getMessage();
        }
    }
}