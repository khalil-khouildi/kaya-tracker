package BinDev.ExpensesTracker.controller;

import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.service.NotificationService;
import BinDev.ExpensesTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @PostMapping("/read/{id}")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/dashboard";
    }

    @PostMapping("/read-all")
    public String markAllAsRead() {
        User user = getCurrentUser();
        notificationService.markAllAsRead(user);
        return "redirect:/dashboard";
    }
}