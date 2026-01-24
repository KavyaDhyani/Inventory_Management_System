package com.example.inventory_service.controller;

import com.example.inventory_service.dto.*;
import com.example.inventory_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get all inventory items")
    public ResponseEntity<Page<StockResponseDTO>> getAllInventory(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAllInventory(pageable));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Adjust stock quantity")
    public ResponseEntity<StockResponseDTO> adjustStock(@Valid @RequestBody StockAdjustRequestDTO dto) {
        return ResponseEntity.ok(inventoryService.adjustStock(dto));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Transfer stock between warehouses")
    public ResponseEntity<StockResponseDTO> transferStock(@Valid @RequestBody StockTransferRequestDTO dto) {
        return ResponseEntity.ok(inventoryService.transferStock(dto));
    }

    @GetMapping("/movements")
    @Operation(summary = "Get stock movements history")
    public ResponseEntity<List<StockMovementResponseDTO>> getStockMovements(
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID warehouseId,
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getStockMovements(productId, warehouseId, pageable));
    }
}
