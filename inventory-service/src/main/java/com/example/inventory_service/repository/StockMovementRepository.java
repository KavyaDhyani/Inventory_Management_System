package com.example.inventory_service.repository;

import com.example.inventory_service.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    Page<StockMovement> findByProductId(UUID productId, Pageable pageable);

    Page<StockMovement> findByWarehouseId(UUID warehouseId, Pageable pageable);

    Page<StockMovement> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId, Pageable pageable);

    @Query("SELECT sm.productId, SUM(sm.quantity) as totalQty, COUNT(sm) as totalMoves " +
           "FROM StockMovement sm " +
           "WHERE sm.createdAt >= :since " +
           "GROUP BY sm.productId " +
           "ORDER BY totalQty DESC")
    List<Object[]> findTopMovedProducts(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT CAST(sm.createdAt AS LocalDate), sm.type, SUM(sm.quantity) " +
           "FROM StockMovement sm " +
           "WHERE sm.createdAt >= :since " +
           "GROUP BY CAST(sm.createdAt AS LocalDate), sm.type " +
           "ORDER BY CAST(sm.createdAt AS LocalDate)")
    List<Object[]> getDailyStockFlow(@Param("since") LocalDateTime since);
}
