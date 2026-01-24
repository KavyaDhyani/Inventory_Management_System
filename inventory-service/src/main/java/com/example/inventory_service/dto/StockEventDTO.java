package com.example.inventory_service.dto;

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
}
