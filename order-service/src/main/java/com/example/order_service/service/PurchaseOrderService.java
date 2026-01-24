package com.example.order_service.service;

import com.example.order_service.dto.*;
import com.example.order_service.exception.BadRequestException;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.kafka.StockEventProducer;
import com.example.order_service.model.PurchaseItem;
import com.example.order_service.model.PurchaseOrder;
import com.example.order_service.repository.PurchaseOrderRepository;
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
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final StockEventProducer stockEventProducer;

    @Transactional
    public PurchaseOrderResponseDTO createPurchaseOrder(PurchaseOrderCreateDTO dto) {
        PurchaseOrder order = PurchaseOrder.builder()
                .supplierName(dto.getSupplierName())
                .status(PurchaseOrder.OrderStatus.CREATED)
                .build();

        for (PurchaseItemDTO itemDto : dto.getItems()) {
            PurchaseItem item = PurchaseItem.builder()
                    .productId(itemDto.getProductId())
                    .warehouseId(itemDto.getWarehouseId())
                    .quantity(itemDto.getQuantity())
                    .unitCost(itemDto.getUnitCost())
                    .build();
            order.addItem(item);
        }

        order = purchaseOrderRepository.save(order);
        log.info("Created purchase order: {}", order.getId());
        return PurchaseOrderResponseDTO.fromEntity(order);
    }

    public PurchaseOrderResponseDTO getPurchaseOrderById(UUID id) {
        PurchaseOrder order = purchaseOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
        return PurchaseOrderResponseDTO.fromEntity(order);
    }

    public Page<PurchaseOrderResponseDTO> getAllPurchaseOrders(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable)
                .map(PurchaseOrderResponseDTO::fromEntity);
    }

    @Transactional
    public PurchaseOrderResponseDTO receivePurchaseOrder(UUID id) {
        PurchaseOrder order = purchaseOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));

        if (order.getStatus() == PurchaseOrder.OrderStatus.RECEIVED) {
            throw new BadRequestException("Purchase order has already been received");
        }

        order.setStatus(PurchaseOrder.OrderStatus.RECEIVED);
        order = purchaseOrderRepository.save(order);

        for (PurchaseItem item : order.getItems()) {
            StockEventDTO event = StockEventDTO.stockInEvent(
                    item.getProductId(),
                    item.getWarehouseId(),
                    item.getQuantity(),
                    order.getId()
            );
            stockEventProducer.publishStockInEvent(event);
        }

        log.info("Received purchase order: {}", order.getId());
        return PurchaseOrderResponseDTO.fromEntity(order);
    }
}
