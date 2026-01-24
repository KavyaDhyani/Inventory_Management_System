package com.example.inventory_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movements", indexes = {
        @Index(name = "idx_movement_product_warehouse", columnList = "product_id, warehouse_id"),
        @Index(name = "idx_movement_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum MovementType {
        IN,
        OUT,
        ADJUST,
        TRANSFER_IN,
        TRANSFER_OUT
    }
}
