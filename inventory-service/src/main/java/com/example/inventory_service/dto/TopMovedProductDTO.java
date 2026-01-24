package com.example.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopMovedProductDTO {

    private UUID productId;
    private String productName;
    private String productSku;
    private Long totalMovements;
    private Long totalQuantityMoved;
}
