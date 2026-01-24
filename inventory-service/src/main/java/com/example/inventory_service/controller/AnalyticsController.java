package com.example.inventory_service.controller;

import com.example.inventory_service.dto.*;
import com.example.inventory_service.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@Tag(name = "Analytics", description = "Analytics and reporting APIs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    @Operation(summary = "Get inventory summary")
    public ResponseEntity<AnalyticsSummaryDTO> getSummary() {
        return ResponseEntity.ok(analyticsService.getSummary());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock items")
    public ResponseEntity<List<LowStockDTO>> getLowStockItems(
            @RequestParam(required = false) Integer threshold) {
        return ResponseEntity.ok(analyticsService.getLowStockItems(threshold));
    }

    @GetMapping("/top-moved")
    @Operation(summary = "Get top moved products")
    public ResponseEntity<List<TopMovedProductDTO>> getTopMovedProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(analyticsService.getTopMovedProducts(limit));
    }

    @GetMapping("/daily-stock-flow")
    @Operation(summary = "Get daily stock flow")
    public ResponseEntity<List<DailyStockFlowDTO>> getDailyStockFlow(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(analyticsService.getDailyStockFlow(days));
    }
}
