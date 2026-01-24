package com.example.inventory_service.dto;

import com.example.inventory_service.model.Product;
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
public class ProductResponseDTO {

    private UUID id;
    private String sku;
    private String name;
    private String description;
    private String category;
    private BigDecimal unitPrice;
    private Integer reorderLevel;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<String> imageUrls;

    public static ProductResponseDTO fromEntity(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .unitPrice(product.getUnitPrice())
                .reorderLevel(product.getReorderLevel())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .imageUrls(product.getImages() != null ?
                        product.getImages().stream()
                                .map(img -> "/api/products/" + product.getId() + "/images/" + img.getId())
                                .collect(Collectors.toList()) : List.of())
                .build();
    }
}
