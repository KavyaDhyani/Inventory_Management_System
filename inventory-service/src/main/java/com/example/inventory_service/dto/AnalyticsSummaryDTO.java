package com.example.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSummaryDTO {

    private Long totalProducts;
    private Long totalWarehouses;
    private Long totalStockUnits;
    private BigDecimal totalStockValue;
}
