package BinDev.ExpensesTracker.controller;

import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final PiggyBankService piggyBankService;
    private final StreakService streakService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        try {
            String encodedPassword = passwordEncoder.encode(password);
            User user = userService.registerUser(firstName, lastName, email, encodedPassword);

            categoryService.createDefaultCategoriesForUser(user);
            piggyBankService.createPiggyBankForUser(user);
            piggyBankService.createDefaultGoalsForUser(user);
            streakService.createStreakForUser(user);

            emailService.sendWelcomeEmail(user);

            return "redirect:/login?success=true";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}