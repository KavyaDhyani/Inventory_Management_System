package com.example.inventory_service.dto;

import com.example.inventory_service.model.StockMovement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementResponseDTO {

    private UUID id;
    private UUID productId;
    private UUID warehouseId;
    private String type;
    private Integer quantity;
    private UUID referenceId;
    private String reason;
    private LocalDateTime createdAt;

    public static StockMovementResponseDTO fromEntity(StockMovement movement) {
        return StockMovementResponseDTO.builder()
                .id(movement.getId())
                .productId(movement.getProductId())
                .warehouseId(movement.getWarehouseId())
                .type(movement.getType().name())
                .quantity(movement.getQuantity())
                .referenceId(movement.getReferenceId())
                .reason(movement.getReason())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}
