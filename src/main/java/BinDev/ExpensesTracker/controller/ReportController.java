package BinDev.ExpensesTracker.controller;

import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.service.ReportService;
import BinDev.ExpensesTracker.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.YearMonth;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String showReportsPage(Model model) {
        User user = getCurrentUser();
        YearMonth now = YearMonth.now();

        model.addAttribute("currentMonth", now.getMonthValue());
        model.addAttribute("currentYear", now.getYear());
        model.addAttribute("page", "reports/index");
        model.addAttribute("userFirstName", user.getFirstName());
        model.addAttribute("pageTitle", "Rapports");

        return "layout/base";
    }

    @GetMapping("/export/pdf")
    public void exportPDF(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            HttpServletResponse response) throws IOException {

        User user = getCurrentUser();

        YearMonth now = YearMonth.now();
        int selectedMonth = (month != null) ? month : now.getMonthValue();
        int selectedYear = (year != null) ? year : now.getYear();

        byte[] pdfBytes = reportService.generateExpenseReport(user, selectedYear, selectedMonth);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expenses_" + selectedYear + "_" + selectedMonth + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }

    @GetMapping("/export/csv")
    public void exportCSV(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            HttpServletResponse response) throws IOException {

        User user = getCurrentUser();

        YearMonth now = YearMonth.now();
        int selectedMonth = (month != null) ? month : now.getMonthValue();
        int selectedYear = (year != null) ? year : now.getYear();

        String csvContent = reportService.generateExpenseCSV(user, selectedYear, selectedMonth);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=expenses_" + selectedYear + "_" + selectedMonth + ".csv");
        response.getWriter().write(csvContent);
    }
}