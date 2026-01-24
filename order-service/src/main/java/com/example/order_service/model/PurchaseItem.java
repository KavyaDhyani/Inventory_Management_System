package com.example.order_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "purchase_items", indexes = {
        @Index(name = "idx_purchase_item_order", columnList = "purchase_order_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost", precision = 12, scale = 2)
    private BigDecimal unitCost;
}
