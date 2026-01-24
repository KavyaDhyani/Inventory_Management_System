package com.example.order_service.dto;

import com.example.order_service.model.SalesOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderResponseDTO {

    private UUID id;
    private String customerName;
    private String status;
    private LocalDateTime createdAt;
    private List<SalesItemResponseDTO> items;
    private BigDecimal totalAmount;

    public static SalesOrderResponseDTO fromEntity(SalesOrder order) {
        List<SalesItemResponseDTO> itemDtos = order.getItems().stream()
                .map(item -> SalesItemResponseDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .warehouseId(item.getWarehouseId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = order.getItems().stream()
                .filter(item -> item.getUnitPrice() != null)
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SalesOrderResponseDTO.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .items(itemDtos)
                .totalAmount(total)
                .build();
    }
}
