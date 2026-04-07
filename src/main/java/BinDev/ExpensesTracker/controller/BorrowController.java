package BinDev.ExpensesTracker.controller;

import BinDev.ExpensesTracker.entity.BorrowTransaction;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.service.BorrowService;
import BinDev.ExpensesTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;
    private final UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String showBorrowPage(Model model) {
        User user = getCurrentUser();

        List<BorrowTransaction> borrows = borrowService.getUserBorrows(user);
        List<BorrowTransaction> pendingBorrows = borrowService.getPendingBorrows(user);
        BigDecimal totalPending = borrowService.getTotalPendingBorrow(user);
        BigDecimal maxBorrow = borrowService.calculateMaxBorrowAmount(user);

        model.addAttribute("borrows", borrows);
        model.addAttribute("pendingBorrows", pendingBorrows);
        model.addAttribute("totalPending", totalPending);
        model.addAttribute("maxBorrow", maxBorrow);
        model.addAttribute("page", "borrow/index");

        return "layout/base";
    }
}