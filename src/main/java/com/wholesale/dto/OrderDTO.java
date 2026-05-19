package com.wholesale.dto;

import com.wholesale.entity.Order;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    private Long id;
    private String orderNumber;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    private String customerName;
    private String status;
    private BigDecimal totalAmount;
    
    @Size(max = 500, message = "Shipping address must not exceed 500 characters")
    private String shippingAddress;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    private LocalDateTime orderDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
    private Long createdById;
    private String createdByName;
    private List<OrderItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static OrderDTO fromEntity(Order order) {
        return OrderDTO.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .customerId(order.getCustomer().getId())
            .customerName(order.getCustomer().getFullName())
            .status(order.getStatus().name())
            .totalAmount(order.getTotalAmount())
            .shippingAddress(order.getShippingAddress())
            .notes(order.getNotes())
            .orderDate(order.getOrderDate())
            .shippedDate(order.getShippedDate())
            .deliveredDate(order.getDeliveredDate())
            .createdById(order.getCreatedBy() != null ? order.getCreatedBy().getId() : null)
            .createdByName(order.getCreatedBy() != null ? order.getCreatedBy().getFullName() : null)
            .items(order.getItems().stream().map(OrderItemDTO::fromEntity).toList())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
