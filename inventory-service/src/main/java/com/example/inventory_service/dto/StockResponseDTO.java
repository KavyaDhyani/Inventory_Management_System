package com.example.inventory_service.dto;

import com.example.inventory_service.model.InventoryStock;
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
public class StockResponseDTO {

    private UUID id;
    private UUID productId;
    private String productName;
    private String productSku;
    private UUID warehouseId;
    private String warehouseName;
    private Integer quantity;
    private LocalDateTime updatedAt;

    public static StockResponseDTO fromEntity(InventoryStock stock) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .productId(stock.getProduct().getId())
                .productName(stock.getProduct().getName())
                .productSku(stock.getProduct().getSku())
                .warehouseId(stock.getWarehouse().getId())
                .warehouseName(stock.getWarehouse().getName())
                .quantity(stock.getQuantity())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
}
