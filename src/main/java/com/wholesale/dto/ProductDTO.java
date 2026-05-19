package com.wholesale.dto;

import com.wholesale.entity.Product;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    private Long id;
    
    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must not exceed 150 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;
    
    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Size(max = 50, message = "Size must not exceed 50 characters")
    private String size;
    
    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;
    
    private boolean active;
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = ProductDTO.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .category(product.getCategory())
            .sku(product.getSku())
            .price(product.getPrice())
            .size(product.getSize())
            .color(product.getColor())
            .active(product.isActive())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
        
        if (product.getInventory() != null) {
            dto.setStockQuantity(product.getInventory().getQuantity());
        }
        
        return dto;
    }
    
    public Product toEntity() {
        return Product.builder()
            .id(this.id)
            .name(this.name)
            .description(this.description)
            .category(this.category)
            .sku(this.sku)
            .price(this.price)
            .size(this.size)
            .color(this.color)
            .active(this.active)
            .build();
    }
}
