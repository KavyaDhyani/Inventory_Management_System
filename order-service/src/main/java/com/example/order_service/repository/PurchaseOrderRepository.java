package com.example.order_service.repository;

import com.example.order_service.model.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    @Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.items WHERE po.id = :id")
    Optional<PurchaseOrder> findByIdWithItems(UUID id);

    Page<PurchaseOrder> findByStatus(PurchaseOrder.OrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findBySupplierNameContainingIgnoreCase(String supplierName, Pageable pageable);
}
