package com.wholesale.controller;

import com.wholesale.dto.DashboardStats;
import com.wholesale.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    @GetMapping("/")
    public String indexPage() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        return "dashboard";
    }
    
    @GetMapping("/customers-page")
    public String customersPage() {
        return "customers";
    }
    
    @GetMapping("/products-page")
    public String productsPage() {
        return "products";
    }
    
    @GetMapping("/inventory-page")
    public String inventoryPage() {
        return "inventory";
    }
    
    @GetMapping("/orders-page")
    public String ordersPage() {
        return "orders";
    }
}
