package com.example.order_service.repository;

import com.example.order_service.model.SalesOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {

    @Query("SELECT so FROM SalesOrder so LEFT JOIN FETCH so.items WHERE so.id = :id")
    Optional<SalesOrder> findByIdWithItems(UUID id);

    Page<SalesOrder> findByStatus(SalesOrder.OrderStatus status, Pageable pageable);

    Page<SalesOrder> findByCustomerNameContainingIgnoreCase(String customerName, Pageable pageable);
}
