package com.wholesale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    
    private long totalCustomers;
    private long totalProducts;
    private long totalOrders;
    private long pendingOrders;
    private long processingOrders;
    private long shippedOrders;
    private long deliveredOrders;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private long lowStockItems;
    private long outOfStockItems;
    private Map<String, Long> ordersByStatus;
}
