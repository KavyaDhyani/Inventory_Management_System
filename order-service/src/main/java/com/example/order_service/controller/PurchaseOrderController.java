package com.example.order_service.controller;

import com.example.order_service.dto.PurchaseOrderCreateDTO;
import com.example.order_service.dto.PurchaseOrderResponseDTO;
import com.example.order_service.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Orders", description = "Purchase order management APIs")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new purchase order")
    public ResponseEntity<PurchaseOrderResponseDTO> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderCreateDTO dto) {
        return new ResponseEntity<>(purchaseOrderService.createPurchaseOrder(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get purchase order by ID")
    public ResponseEntity<PurchaseOrderResponseDTO> getPurchaseOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all purchase orders")
    public ResponseEntity<Page<PurchaseOrderResponseDTO>> getAllPurchaseOrders(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders(pageable));
    }

    @PatchMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Receive a purchase order (increases stock)")
    public ResponseEntity<PurchaseOrderResponseDTO> receivePurchaseOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.receivePurchaseOrder(id));
    }
}
