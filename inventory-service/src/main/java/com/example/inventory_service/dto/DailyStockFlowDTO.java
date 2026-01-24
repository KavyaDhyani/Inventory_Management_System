package com.example.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStockFlowDTO {

    private LocalDate date;
    private Long stockIn;
    private Long stockOut;
    private Long netFlow;
}
