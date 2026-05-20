package com.wholesale.service;

import com.wholesale.dto.DashboardStats;
import com.wholesale.entity.Inventory;
import com.wholesale.entity.Order;
import com.wholesale.repository.CustomerRepository;
import com.wholesale.repository.InventoryRepository;
import com.wholesale.repository.OrderRepository;
import com.wholesale.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void testGetDashboardStats() {
        when(customerRepository.count()).thenReturn(100L);
        when(productRepository.count()).thenReturn(50L);
        when(orderRepository.count()).thenReturn(200L);
        
        when(orderRepository.countByStatus(Order.OrderStatus.PENDING)).thenReturn(10L);
        when(orderRepository.countByStatus(Order.OrderStatus.PROCESSING)).thenReturn(5L);
        when(orderRepository.countByStatus(Order.OrderStatus.SHIPPED)).thenReturn(15L);
        when(orderRepository.countByStatus(Order.OrderStatus.DELIVERED)).thenReturn(160L);
        when(orderRepository.countByStatus(Order.OrderStatus.CANCELLED)).thenReturn(10L);
        
        when(orderRepository.getTotalRevenue()).thenReturn(new BigDecimal("10000.00"));
        when(orderRepository.getRevenueByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("2000.00"));
                
        when(inventoryRepository.findLowStockItems()).thenReturn(List.of(new Inventory(), new Inventory()));
        when(inventoryRepository.findOutOfStockItems()).thenReturn(List.of(new Inventory()));

        DashboardStats stats = dashboardService.getDashboardStats();

        assertNotNull(stats);
        assertEquals(100L, stats.getTotalCustomers());
        assertEquals(50L, stats.getTotalProducts());
        assertEquals(200L, stats.getTotalOrders());
        assertEquals(10L, stats.getPendingOrders());
        assertEquals(new BigDecimal("10000.00"), stats.getTotalRevenue());
        assertEquals(new BigDecimal("2000.00"), stats.getMonthlyRevenue());
        assertEquals(2L, stats.getLowStockItems());
        assertEquals(1L, stats.getOutOfStockItems());
        assertEquals(160L, stats.getOrdersByStatus().get("DELIVERED"));
    }
}
