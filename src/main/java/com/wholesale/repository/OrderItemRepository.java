package com.wholesale.repository;

import com.wholesale.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long getTotalQuantitySoldByProduct(@Param("productId") Long productId);
    
    @Query("SELECT oi.product.id, SUM(oi.quantity) as totalQty FROM OrderItem oi " +
           "GROUP BY oi.product.id ORDER BY totalQty DESC")
    List<Object[]> getTopSellingProducts();
}
