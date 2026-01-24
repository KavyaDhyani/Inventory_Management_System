package com.example.order_service.controller;

import com.example.order_service.dto.SalesOrderCreateDTO;
import com.example.order_service.dto.SalesOrderResponseDTO;
import com.example.order_service.service.SalesOrderService;
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
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Sales Orders", description = "Sales order management APIs")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Create a new sales order")
    public ResponseEntity<SalesOrderResponseDTO> createSalesOrder(
            @Valid @RequestBody SalesOrderCreateDTO dto) {
        return new ResponseEntity<>(salesOrderService.createSalesOrder(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get sales order by ID")
    public ResponseEntity<SalesOrderResponseDTO> getSalesOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(salesOrderService.getSalesOrderById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all sales orders")
    public ResponseEntity<Page<SalesOrderResponseDTO>> getAllSalesOrders(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(salesOrderService.getAllSalesOrders(pageable));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Confirm a sales order (decreases stock)")
    public ResponseEntity<SalesOrderResponseDTO> confirmSalesOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(salesOrderService.confirmSalesOrder(id));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cancel a sales order")
    public ResponseEntity<SalesOrderResponseDTO> cancelSalesOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(salesOrderService.cancelSalesOrder(id));
    }
}
