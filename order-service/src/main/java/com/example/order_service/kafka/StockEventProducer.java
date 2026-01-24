package com.example.order_service.kafka;

import com.example.order_service.dto.StockEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockEventProducer {

    private static final String STOCK_IN_TOPIC = "stock.in.events";
    private static final String STOCK_OUT_TOPIC = "stock.out.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishStockInEvent(StockEventDTO event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(STOCK_IN_TOPIC, event.getProductId().toString(), message);
            log.info("Published stock IN event: {}", event.getEventId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize stock IN event", e);
            throw new RuntimeException("Failed to publish stock event", e);
        }
    }

    public void publishStockOutEvent(StockEventDTO event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(STOCK_OUT_TOPIC, event.getProductId().toString(), message);
            log.info("Published stock OUT event: {}", event.getEventId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize stock OUT event", e);
            throw new RuntimeException("Failed to publish stock event", e);
        }
    }
}
