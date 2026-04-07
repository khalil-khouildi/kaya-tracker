package BinDev.ExpensesTracker.controller;

import BinDev.ExpensesTracker.dto.ChartDataDto;
import BinDev.ExpensesTracker.entity.Notification;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.service.DashboardService;
import BinDev.ExpensesTracker.service.NotificationService;
import BinDev.ExpensesTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;
    private final NotificationService notificationService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String showDashboard(Model model) {
        User user = getCurrentUser();

        DashboardService.DashboardData data = dashboardService.getDashboardData(user);
        DashboardService.ChartData chartData = dashboardService.getChartData(user);

        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(user);

        // Créer le DTO
        ChartDataDto chartDataDto = new ChartDataDto(
                chartData.categoryNames,
                chartData.categoryAmounts,
                chartData.days,
                chartData.dailyAmounts
        );

        model.addAttribute("data", data);
        model.addAttribute("chartDataDto", chartDataDto);  // ← Utiliser le DTO
        model.addAttribute("unreadCount", notificationService.getUnreadNotifications(user).size());
        model.addAttribute("page", "dashboard/index");
        model.addAttribute("userFirstName", user.getFirstName());
        model.addAttribute("unreadNotifications", unreadNotifications);  // ← AJOUTER CETTE LIGNE

        return "layout/base";
    }

    @GetMapping("/category/{categoryId}")
    public String showCategoryExpenses(@PathVariable Long categoryId, Model model) {
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("page", "dashboard/category-expenses");
        return "layout/base";
    }

}