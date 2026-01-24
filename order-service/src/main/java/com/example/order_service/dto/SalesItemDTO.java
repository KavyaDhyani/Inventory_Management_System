package com.example.order_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class SalesItemDTO {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
}
