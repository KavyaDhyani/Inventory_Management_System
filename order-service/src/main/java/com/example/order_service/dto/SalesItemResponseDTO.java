package com.example.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesItemResponseDTO {

    private UUID id;
    private UUID productId;
    private UUID warehouseId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
