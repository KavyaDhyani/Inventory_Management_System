package com.example.order_service.dto;

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
public class StockEventDTO {

    private UUID eventId;
    private String eventType;
    private UUID productId;
    private UUID warehouseId;
    private Integer quantity;
    private UUID referenceId;
    private LocalDateTime timestamp;

    public static StockEventDTO stockInEvent(UUID productId, UUID warehouseId, Integer quantity, UUID referenceId) {
        return StockEventDTO.builder()
                .eventId(UUID.randomUUID())
                .eventType("STOCK_IN_EVENT")
                .productId(productId)
                .warehouseId(warehouseId)
                .quantity(quantity)
                .referenceId(referenceId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static StockEventDTO stockOutEvent(UUID productId, UUID warehouseId, Integer quantity, UUID referenceId) {
        return StockEventDTO.builder()
                .eventId(UUID.randomUUID())
                .eventType("STOCK_OUT_EVENT")
                .productId(productId)
                .warehouseId(warehouseId)
                .quantity(quantity)
                .referenceId(referenceId)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
