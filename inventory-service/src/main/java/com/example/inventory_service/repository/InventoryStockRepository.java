package com.example.inventory_service.repository;

import com.example.inventory_service.model.InventoryStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, UUID> {

    Optional<InventoryStock> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);

    List<InventoryStock> findByProductId(UUID productId);

    List<InventoryStock> findByWarehouseId(UUID warehouseId);

    @Query("SELECT i FROM InventoryStock i JOIN FETCH i.product JOIN FETCH i.warehouse")
    Page<InventoryStock> findAllWithDetails(Pageable pageable);

    @Query("SELECT i FROM InventoryStock i JOIN FETCH i.product p JOIN FETCH i.warehouse " +
           "WHERE i.quantity <= p.reorderLevel")
    List<InventoryStock> findLowStockItems();

    @Query("SELECT i FROM InventoryStock i JOIN FETCH i.product p JOIN FETCH i.warehouse " +
           "WHERE i.quantity <= :threshold")
    List<InventoryStock> findByQuantityLessThanEqual(@Param("threshold") Integer threshold);

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM InventoryStock i")
    Long getTotalStockUnits();

    @Query("SELECT COALESCE(SUM(i.quantity * i.product.unitPrice), 0) FROM InventoryStock i")
    BigDecimal getTotalStockValue();
}
