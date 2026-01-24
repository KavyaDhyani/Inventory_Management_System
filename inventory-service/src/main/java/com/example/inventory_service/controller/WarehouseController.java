package com.example.inventory_service.controller;

import com.example.inventory_service.dto.WarehouseCreateDTO;
import com.example.inventory_service.dto.WarehouseResponseDTO;
import com.example.inventory_service.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouses", description = "Warehouse management APIs")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new warehouse")
    public ResponseEntity<WarehouseResponseDTO> createWarehouse(@Valid @RequestBody WarehouseCreateDTO dto) {
        return new ResponseEntity<>(warehouseService.createWarehouse(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all warehouses")
    public ResponseEntity<List<WarehouseResponseDTO>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID")
    public ResponseEntity<WarehouseResponseDTO> getWarehouse(@PathVariable UUID id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }
}
