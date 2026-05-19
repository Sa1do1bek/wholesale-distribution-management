package com.wholesale.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_inventory_warehouse", columnList = "warehouse_location"),
    @Index(name = "idx_inventory_low_stock", columnList = "quantity")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "min_quantity", nullable = false)
    private Integer minQuantity = 10;
    
    @Column(name = "max_quantity")
    private Integer maxQuantity;
    
    @Column(name = "warehouse_location", length = 100)
    private String warehouseLocation;
    
    @Column(name = "shelf_number", length = 50)
    private String shelfNumber;
    
    @Column(name = "last_restocked")
    private LocalDateTime lastRestocked;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isLowStock() {
        return quantity != null && minQuantity != null && quantity <= minQuantity;
    }
}
