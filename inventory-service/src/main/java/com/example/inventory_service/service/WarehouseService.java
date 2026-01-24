package com.example.inventory_service.service;

import com.example.inventory_service.dto.WarehouseCreateDTO;
import com.example.inventory_service.dto.WarehouseResponseDTO;
import com.example.inventory_service.exception.ConflictException;
import com.example.inventory_service.exception.ResourceNotFoundException;
import com.example.inventory_service.model.Warehouse;
import com.example.inventory_service.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional
    public WarehouseResponseDTO createWarehouse(WarehouseCreateDTO dto) {
        if (warehouseRepository.existsByName(dto.getName())) {
            throw new ConflictException("Warehouse with name " + dto.getName() + " already exists");
        }

        Warehouse warehouse = Warehouse.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .build();

        warehouse = warehouseRepository.save(warehouse);
        log.info("Created warehouse with ID: {}", warehouse.getId());
        return WarehouseResponseDTO.fromEntity(warehouse);
    }

    @Cacheable(value = "warehouses")
    public List<WarehouseResponseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(WarehouseResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public WarehouseResponseDTO getWarehouseById(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return WarehouseResponseDTO.fromEntity(warehouse);
    }

    public Warehouse getWarehouseEntityById(UUID id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
    }
}
