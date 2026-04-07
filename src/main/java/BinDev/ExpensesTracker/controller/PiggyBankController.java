package BinDev.ExpensesTracker.controller;

import BinDev.ExpensesTracker.entity.PiggyBank;
import BinDev.ExpensesTracker.entity.PiggyBankGoal;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.service.PiggyBankService;
import BinDev.ExpensesTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/piggybank")
@RequiredArgsConstructor
public class PiggyBankController {

    private final PiggyBankService piggyBankService;
    private final UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String showPiggyBank(Model model) {
        User user = getCurrentUser();

        PiggyBank piggyBank = piggyBankService.findByUser(user);
        PiggyBankGoal activeGoal = piggyBankService.getActiveGoal(user);
        List<PiggyBankGoal> allGoals = piggyBankService.getAllGoals(user);
        List<PiggyBankGoal> completedGoals = piggyBankService.getCompletedGoals(user);

        model.addAttribute("piggyBank", piggyBank);
        model.addAttribute("activeGoal", activeGoal);
        model.addAttribute("allGoals", allGoals);
        model.addAttribute("completedGoals", completedGoals);
        model.addAttribute("page", "piggybank/index");

        return "layout/base";
    }

    @PostMapping("/complete/{id}")
    public String completeGoal(@PathVariable Long id, Model model) {
        try {
            User user = getCurrentUser();
            piggyBankService.completeGoal(user, id);
            return "redirect:/piggybank?goalCompleted=true";

        } catch (RuntimeException e) {
            return "redirect:/piggybank?error=" + e.getMessage();
        }
    }
}