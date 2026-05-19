package com.wholesale.controller;

import com.wholesale.dto.CreateOrderRequest;
import com.wholesale.dto.OrderDTO;
import com.wholesale.dto.OrderItemDTO;
import com.wholesale.dto.PageResponse;
import com.wholesale.entity.Order;
import com.wholesale.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<PageResponse<OrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(orderService.getAllOrders(page, size, sortBy, sortDir));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<PageResponse<OrderDTO>> getOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(orderService.getOrdersByStatus(orderStatus, page, size));
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PageResponse<OrderDTO>> getOrdersByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId, page, size));
    }
    
    @GetMapping("/search")
    public ResponseEntity<PageResponse<OrderDTO>> searchOrders(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.searchOrders(q, page, size));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderDTO> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }
    
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        OrderDTO created = orderService.createOrder(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(orderService.updateOrderStatus(id, orderStatus));
    }
    
    @PostMapping("/{id}/items")
    public ResponseEntity<OrderDTO> addItemToOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemDTO itemDTO) {
        return ResponseEntity.ok(orderService.addItemToOrder(id, itemDTO));
    }
    
    @GetMapping("/stats/count/{status}")
    public ResponseEntity<Long> getOrderCountByStatus(@PathVariable String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(orderService.getOrderCountByStatus(orderStatus));
    }
    
    @GetMapping("/stats/revenue/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(orderService.getTotalRevenue());
    }
    
    @GetMapping("/stats/revenue/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BigDecimal> getMonthlyRevenue() {
        return ResponseEntity.ok(orderService.getMonthlyRevenue());
    }
}
