package com.wholesale.repository;

import com.wholesale.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByProductId(Long productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.minQuantity")
    List<Inventory> findLowStockItems();
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.minQuantity")
    Page<Inventory> findLowStockItems(Pageable pageable);
    
    @Query("SELECT i FROM Inventory i WHERE i.warehouseLocation = :location")
    Page<Inventory> findByWarehouseLocation(@Param("location") String location, Pageable pageable);
    
    @Query("SELECT DISTINCT i.warehouseLocation FROM Inventory i WHERE i.warehouseLocation IS NOT NULL ORDER BY i.warehouseLocation")
    List<String> findAllWarehouseLocations();
    
    @Query("SELECT i FROM Inventory i JOIN FETCH i.product WHERE i.quantity > 0")
    List<Inventory> findAllInStock();
    
    @Query("SELECT SUM(i.quantity) FROM Inventory i")
    Long getTotalInventoryCount();
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity = 0")
    List<Inventory> findOutOfStockItems();
}
