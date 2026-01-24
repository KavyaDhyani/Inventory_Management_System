package com.example.inventory_service.service;

import com.example.inventory_service.dto.*;
import com.example.inventory_service.exception.BadRequestException;
import com.example.inventory_service.exception.InsufficientStockException;
import com.example.inventory_service.exception.ResourceNotFoundException;
import com.example.inventory_service.model.InventoryStock;
import com.example.inventory_service.model.Product;
import com.example.inventory_service.model.StockMovement;
import com.example.inventory_service.model.Warehouse;
import com.example.inventory_service.repository.InventoryStockRepository;
import com.example.inventory_service.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryStockRepository inventoryStockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductService productService;
    private final WarehouseService warehouseService;
    private final EmailService emailService;

    public Page<StockResponseDTO> getAllInventory(Pageable pageable) {
        return inventoryStockRepository.findAllWithDetails(pageable)
                .map(StockResponseDTO::fromEntity);
    }

    @Transactional
    public StockResponseDTO adjustStock(StockAdjustRequestDTO dto) {
        Product product = productService.getProductEntityById(dto.getProductId());
        Warehouse warehouse = warehouseService.getWarehouseEntityById(dto.getWarehouseId());

        InventoryStock stock = inventoryStockRepository
                .findByProductIdAndWarehouseId(dto.getProductId(), dto.getWarehouseId())
                .orElse(InventoryStock.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build());

        int newQuantity = stock.getQuantity() + dto.getQuantity();
        if (newQuantity < 0) {
            throw new InsufficientStockException("Insufficient stock. Current: " + stock.getQuantity() + ", Requested adjustment: " + dto.getQuantity());
        }

        stock.setQuantity(newQuantity);
        stock = inventoryStockRepository.save(stock);

        StockMovement.MovementType type = dto.getQuantity() > 0 ?
                StockMovement.MovementType.IN : StockMovement.MovementType.OUT;

        if (dto.getQuantity() != 0) {
            createMovementRecord(dto.getProductId(), dto.getWarehouseId(),
                    StockMovement.MovementType.ADJUST, Math.abs(dto.getQuantity()),
                    null, dto.getReason());
        }

        checkLowStock(stock, product);

        log.info("Adjusted stock for product {} in warehouse {} by {}",
                dto.getProductId(), dto.getWarehouseId(), dto.getQuantity());
        return StockResponseDTO.fromEntity(stock);
    }

    @Transactional
    public StockResponseDTO transferStock(StockTransferRequestDTO dto) {
        if (dto.getSourceWarehouseId().equals(dto.getDestinationWarehouseId())) {
            throw new BadRequestException("Source and destination warehouses must be different");
        }

        Product product = productService.getProductEntityById(dto.getProductId());
        Warehouse sourceWarehouse = warehouseService.getWarehouseEntityById(dto.getSourceWarehouseId());
        Warehouse destWarehouse = warehouseService.getWarehouseEntityById(dto.getDestinationWarehouseId());

        InventoryStock sourceStock = inventoryStockRepository
                .findByProductIdAndWarehouseId(dto.getProductId(), dto.getSourceWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found in source warehouse"));

        if (sourceStock.getQuantity() < dto.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock in source warehouse. Available: " + sourceStock.getQuantity());
        }

        sourceStock.setQuantity(sourceStock.getQuantity() - dto.getQuantity());
        inventoryStockRepository.save(sourceStock);

        InventoryStock destStock = inventoryStockRepository
                .findByProductIdAndWarehouseId(dto.getProductId(), dto.getDestinationWarehouseId())
                .orElse(InventoryStock.builder()
                        .product(product)
                        .warehouse(destWarehouse)
                        .quantity(0)
                        .build());

        destStock.setQuantity(destStock.getQuantity() + dto.getQuantity());
        destStock = inventoryStockRepository.save(destStock);

        UUID transferId = UUID.randomUUID();
        createMovementRecord(dto.getProductId(), dto.getSourceWarehouseId(),
                StockMovement.MovementType.TRANSFER_OUT, dto.getQuantity(), transferId, dto.getReason());
        createMovementRecord(dto.getProductId(), dto.getDestinationWarehouseId(),
                StockMovement.MovementType.TRANSFER_IN, dto.getQuantity(), transferId, dto.getReason());

        checkLowStock(sourceStock, product);

        log.info("Transferred {} units of product {} from warehouse {} to warehouse {}",
                dto.getQuantity(), dto.getProductId(), dto.getSourceWarehouseId(), dto.getDestinationWarehouseId());
        return StockResponseDTO.fromEntity(destStock);
    }

    @Transactional
    public void processStockIn(UUID productId, UUID warehouseId, Integer quantity, UUID referenceId) {
        Product product = productService.getProductEntityById(productId);
        Warehouse warehouse = warehouseService.getWarehouseEntityById(warehouseId);

        InventoryStock stock = inventoryStockRepository
                .findByProductIdAndWarehouseId(productId, warehouseId)
                .orElse(InventoryStock.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build());

        stock.setQuantity(stock.getQuantity() + quantity);
        inventoryStockRepository.save(stock);

        createMovementRecord(productId, warehouseId, StockMovement.MovementType.IN,
                quantity, referenceId, "Purchase order received");

        log.info("Stock IN: {} units of product {} in warehouse {}", quantity, productId, warehouseId);
    }

    @Transactional
    public void processStockOut(UUID productId, UUID warehouseId, Integer quantity, UUID referenceId) {
        Product product = productService.getProductEntityById(productId);

        InventoryStock stock = inventoryStockRepository
                .findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        if (stock.getQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock. Available: " + stock.getQuantity());
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        inventoryStockRepository.save(stock);

        createMovementRecord(productId, warehouseId, StockMovement.MovementType.OUT,
                quantity, referenceId, "Sales order confirmed");

        checkLowStock(stock, product);

        log.info("Stock OUT: {} units of product {} from warehouse {}", quantity, productId, warehouseId);
    }

    public List<StockMovementResponseDTO> getStockMovements(UUID productId, UUID warehouseId, Pageable pageable) {
        Page<StockMovement> movements;
        if (productId != null && warehouseId != null) {
            movements = stockMovementRepository.findByProductIdAndWarehouseId(productId, warehouseId, pageable);
        } else if (productId != null) {
            movements = stockMovementRepository.findByProductId(productId, pageable);
        } else if (warehouseId != null) {
            movements = stockMovementRepository.findByWarehouseId(warehouseId, pageable);
        } else {
            movements = stockMovementRepository.findAll(pageable);
        }
        return movements.stream()
                .map(StockMovementResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void createMovementRecord(UUID productId, UUID warehouseId,
            StockMovement.MovementType type, Integer quantity, UUID referenceId, String reason) {
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .warehouseId(warehouseId)
                .type(type)
                .quantity(quantity)
                .referenceId(referenceId)
                .reason(reason)
                .build();
        stockMovementRepository.save(movement);
    }

    private void checkLowStock(InventoryStock stock, Product product) {
        if (stock.getQuantity() <= product.getReorderLevel()) {
            log.warn("Low stock alert: Product {} in warehouse {} has {} units (reorder level: {})",
                    product.getSku(), stock.getWarehouse().getName(),
                    stock.getQuantity(), product.getReorderLevel());
            emailService.sendLowStockAlert(product, stock);
        }
    }
}
