package com.example.inventory_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateDTO {

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;

    @Min(value = 0, message = "Reorder level must be non-negative")
    private Integer reorderLevel;
}
