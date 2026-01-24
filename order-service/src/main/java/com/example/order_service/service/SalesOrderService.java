package com.example.order_service.service;

import com.example.order_service.dto.*;
import com.example.order_service.exception.BadRequestException;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.kafka.StockEventProducer;
import com.example.order_service.model.SalesItem;
import com.example.order_service.model.SalesOrder;
import com.example.order_service.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final StockEventProducer stockEventProducer;

    @Transactional
    public SalesOrderResponseDTO createSalesOrder(SalesOrderCreateDTO dto) {
        SalesOrder order = SalesOrder.builder()
                .customerName(dto.getCustomerName())
                .status(SalesOrder.OrderStatus.CREATED)
                .build();

        for (SalesItemDTO itemDto : dto.getItems()) {
            SalesItem item = SalesItem.builder()
                    .productId(itemDto.getProductId())
                    .warehouseId(itemDto.getWarehouseId())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .build();
            order.addItem(item);
        }

        order = salesOrderRepository.save(order);
        log.info("Created sales order: {}", order.getId());
        return SalesOrderResponseDTO.fromEntity(order);
    }

    public SalesOrderResponseDTO getSalesOrderById(UUID id) {
        SalesOrder order = salesOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found with id: " + id));
        return SalesOrderResponseDTO.fromEntity(order);
    }

    public Page<SalesOrderResponseDTO> getAllSalesOrders(Pageable pageable) {
        return salesOrderRepository.findAll(pageable)
                .map(SalesOrderResponseDTO::fromEntity);
    }

    @Transactional
    public SalesOrderResponseDTO confirmSalesOrder(UUID id) {
        SalesOrder order = salesOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found with id: " + id));

        if (order.getStatus() == SalesOrder.OrderStatus.CONFIRMED) {
            throw new BadRequestException("Sales order has already been confirmed");
        }

        if (order.getStatus() == SalesOrder.OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot confirm a cancelled order");
        }

        order.setStatus(SalesOrder.OrderStatus.CONFIRMED);
        order = salesOrderRepository.save(order);

        for (SalesItem item : order.getItems()) {
            StockEventDTO event = StockEventDTO.stockOutEvent(
                    item.getProductId(),
                    item.getWarehouseId(),
                    item.getQuantity(),
                    order.getId()
            );
            stockEventProducer.publishStockOutEvent(event);
        }

        log.info("Confirmed sales order: {}", order.getId());
        return SalesOrderResponseDTO.fromEntity(order);
    }

    @Transactional
    public SalesOrderResponseDTO cancelSalesOrder(UUID id) {
        SalesOrder order = salesOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found with id: " + id));

        if (order.getStatus() == SalesOrder.OrderStatus.CONFIRMED) {
            throw new BadRequestException("Cannot cancel a confirmed order");
        }

        if (order.getStatus() == SalesOrder.OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }

        order.setStatus(SalesOrder.OrderStatus.CANCELLED);
        order = salesOrderRepository.save(order);

        log.info("Cancelled sales order: {}", order.getId());
        return SalesOrderResponseDTO.fromEntity(order);
    }
}
