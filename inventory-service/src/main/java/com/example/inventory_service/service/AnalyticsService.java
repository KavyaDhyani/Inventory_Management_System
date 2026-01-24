package com.example.inventory_service.service;

import com.example.inventory_service.dto.*;
import com.example.inventory_service.model.Product;
import com.example.inventory_service.model.StockMovement;
import com.example.inventory_service.repository.InventoryStockRepository;
import com.example.inventory_service.repository.ProductRepository;
import com.example.inventory_service.repository.StockMovementRepository;
import com.example.inventory_service.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final StockMovementRepository stockMovementRepository;

    @Cacheable(value = "analytics-summary", unless = "#result == null")
    public AnalyticsSummaryDTO getSummary() {
        Long totalProducts = productRepository.countActiveProducts();
        Long totalWarehouses = warehouseRepository.count();
        Long totalStockUnits = inventoryStockRepository.getTotalStockUnits();
        BigDecimal totalStockValue = inventoryStockRepository.getTotalStockValue();

        return AnalyticsSummaryDTO.builder()
                .totalProducts(totalProducts)
                .totalWarehouses(totalWarehouses)
                .totalStockUnits(totalStockUnits)
                .totalStockValue(totalStockValue != null ? totalStockValue : BigDecimal.ZERO)
                .build();
    }

    public List<LowStockDTO> getLowStockItems(Integer threshold) {
        var lowStockItems = threshold != null ?
                inventoryStockRepository.findByQuantityLessThanEqual(threshold) :
                inventoryStockRepository.findLowStockItems();

        return lowStockItems.stream()
                .map(stock -> LowStockDTO.builder()
                        .productId(stock.getProduct().getId())
                        .productName(stock.getProduct().getName())
                        .productSku(stock.getProduct().getSku())
                        .warehouseId(stock.getWarehouse().getId())
                        .warehouseName(stock.getWarehouse().getName())
                        .currentQuantity(stock.getQuantity())
                        .reorderLevel(stock.getProduct().getReorderLevel())
                        .build())
                .collect(Collectors.toList());
    }

    public List<TopMovedProductDTO> getTopMovedProducts(int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Object[]> results = stockMovementRepository.findTopMovedProducts(since, PageRequest.of(0, limit));

        Map<UUID, Product> productCache = new HashMap<>();

        return results.stream()
                .map(row -> {
                    UUID productId = (UUID) row[0];
                    Long totalQty = ((Number) row[1]).longValue();
                    Long totalMoves = ((Number) row[2]).longValue();

                    Product product = productCache.computeIfAbsent(productId,
                            id -> productRepository.findById(id).orElse(null));

                    if (product == null) return null;

                    return TopMovedProductDTO.builder()
                            .productId(productId)
                            .productName(product.getName())
                            .productSku(product.getSku())
                            .totalMovements(totalMoves)
                            .totalQuantityMoved(totalQty)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<DailyStockFlowDTO> getDailyStockFlow(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> results = stockMovementRepository.getDailyStockFlow(since);

        Map<LocalDate, DailyStockFlowDTO> flowMap = new LinkedHashMap<>();

        for (int i = 0; i < days; i++) {
            LocalDate date = LocalDate.now().minusDays(days - 1 - i);
            flowMap.put(date, DailyStockFlowDTO.builder()
                    .date(date)
                    .stockIn(0L)
                    .stockOut(0L)
                    .netFlow(0L)
                    .build());
        }

        for (Object[] row : results) {
            LocalDate date = (LocalDate) row[0];
            StockMovement.MovementType type = (StockMovement.MovementType) row[1];
            Long quantity = ((Number) row[2]).longValue();

            DailyStockFlowDTO dto = flowMap.get(date);
            if (dto != null) {
                if (type == StockMovement.MovementType.IN || type == StockMovement.MovementType.TRANSFER_IN) {
                    dto.setStockIn(dto.getStockIn() + quantity);
                } else if (type == StockMovement.MovementType.OUT || type == StockMovement.MovementType.TRANSFER_OUT) {
                    dto.setStockOut(dto.getStockOut() + quantity);
                }
            }
        }

        flowMap.values().forEach(dto ->
                dto.setNetFlow(dto.getStockIn() - dto.getStockOut()));

        return new ArrayList<>(flowMap.values());
    }
}
