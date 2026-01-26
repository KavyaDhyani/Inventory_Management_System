package com.example.order_service.kafka;

import com.example.order_service.dto.StockEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${kafka.enabled:false}")
    private boolean kafkaEnabled;

    public void publishStockInEvent(StockEventDTO event) {
        if (!kafkaEnabled) {
            log.info("Kafka disabled. Stock IN event would be published: {}", event.getEventId());
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(STOCK_IN_TOPIC, event.getProductId().toString(), message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish stock IN event: {}", event.getEventId(), ex);
                        } else {
                            log.info("Published stock IN event: {} to partition {}",
                                    event.getEventId(), result.getRecordMetadata().partition());
                        }
                    });
            log.info("Sent stock IN event: {}", event.getEventId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize stock IN event", e);
        } catch (Exception e) {
            log.error("Failed to publish stock IN event: {}", event.getEventId(), e);
        }
    }

    public void publishStockOutEvent(StockEventDTO event) {
        if (!kafkaEnabled) {
            log.info("Kafka disabled. Stock OUT event would be published: {}", event.getEventId());
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(STOCK_OUT_TOPIC, event.getProductId().toString(), message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish stock OUT event: {}", event.getEventId(), ex);
                        } else {
                            log.info("Published stock OUT event: {} to partition {}",
                                    event.getEventId(), result.getRecordMetadata().partition());
                        }
                    });
            log.info("Sent stock OUT event: {}", event.getEventId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize stock OUT event", e);
        } catch (Exception e) {
            log.error("Failed to publish stock OUT event: {}", event.getEventId(), e);
        }
    }
}
