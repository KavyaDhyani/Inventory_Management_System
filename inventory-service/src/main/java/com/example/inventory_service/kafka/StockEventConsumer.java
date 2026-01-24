package com.example.inventory_service.kafka;

import com.example.inventory_service.dto.StockEventDTO;
import com.example.inventory_service.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockEventConsumer {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "stock.in.events", groupId = "${spring.kafka.consumer.group-id:inventory-service}")
    public void consumeStockInEvent(String message) {
        try {
            StockEventDTO event = objectMapper.readValue(message, StockEventDTO.class);
            log.info("Received stock IN event: {}", event.getEventId());

            inventoryService.processStockIn(
                    event.getProductId(),
                    event.getWarehouseId(),
                    event.getQuantity(),
                    event.getReferenceId()
            );

            log.info("Processed stock IN event successfully: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Failed to process stock IN event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "stock.out.events", groupId = "${spring.kafka.consumer.group-id:inventory-service}")
    public void consumeStockOutEvent(String message) {
        try {
            StockEventDTO event = objectMapper.readValue(message, StockEventDTO.class);
            log.info("Received stock OUT event: {}", event.getEventId());

            inventoryService.processStockOut(
                    event.getProductId(),
                    event.getWarehouseId(),
                    event.getQuantity(),
                    event.getReferenceId()
            );

            log.info("Processed stock OUT event successfully: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Failed to process stock OUT event: {}", e.getMessage(), e);
        }
    }
}
