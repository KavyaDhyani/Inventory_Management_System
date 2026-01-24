package com.example.order_service.dto;

import com.example.order_service.model.PurchaseOrder;
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
public class PurchaseOrderResponseDTO {

    private UUID id;
    private String supplierName;
    private String status;
    private LocalDateTime createdAt;
    private List<PurchaseItemResponseDTO> items;
    private BigDecimal totalAmount;

    public static PurchaseOrderResponseDTO fromEntity(PurchaseOrder order) {
        List<PurchaseItemResponseDTO> itemDtos = order.getItems().stream()
                .map(item -> PurchaseItemResponseDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .warehouseId(item.getWarehouseId())
                        .quantity(item.getQuantity())
                        .unitCost(item.getUnitCost())
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = order.getItems().stream()
                .filter(item -> item.getUnitCost() != null)
                .map(item -> item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PurchaseOrderResponseDTO.builder()
                .id(order.getId())
                .supplierName(order.getSupplierName())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .items(itemDtos)
                .totalAmount(total)
                .build();
    }
}
