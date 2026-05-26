package com.wholesale.service;

import com.wholesale.dto.DashboardStats;
import com.wholesale.entity.Order;
import com.wholesale.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    
    public DashboardStats getDashboardStats() {
        long totalCustomers = customerRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long processingOrders = orderRepository.countByStatus(Order.OrderStatus.PROCESSING);
        long shippedOrders = orderRepository.countByStatus(Order.OrderStatus.SHIPPED);
        long deliveredOrders = orderRepository.countByStatus(Order.OrderStatus.DELIVERED);
        
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        BigDecimal monthlyRevenue = orderRepository.getRevenueByDateRange(startOfMonth, LocalDateTime.now());
        if (monthlyRevenue == null) monthlyRevenue = BigDecimal.ZERO;
        
        long lowStockItems = inventoryRepository.findLowStockItems().size();
        long outOfStockItems = inventoryRepository.findOutOfStockItems().size();
        
        Map<String, Long> ordersByStatus = new HashMap<>();
        ordersByStatus.put("PENDING", pendingOrders);
        ordersByStatus.put("PROCESSING", processingOrders);
        ordersByStatus.put("SHIPPED", shippedOrders);
        ordersByStatus.put("DELIVERED", deliveredOrders);
        ordersByStatus.put("CANCELLED", orderRepository.countByStatus(Order.OrderStatus.CANCELLED));
        
        return DashboardStats.builder()
            .totalCustomers(totalCustomers)
            .totalProducts(totalProducts)
            .totalOrders(totalOrders)
            .pendingOrders(pendingOrders)
            .processingOrders(processingOrders)
            .shippedOrders(shippedOrders)
            .deliveredOrders(deliveredOrders)
            .totalRevenue(totalRevenue)
            .monthlyRevenue(monthlyRevenue)
            .lowStockItems(lowStockItems)
            .outOfStockItems(outOfStockItems)
            .ordersByStatus(ordersByStatus)
            .build();

    }
}
