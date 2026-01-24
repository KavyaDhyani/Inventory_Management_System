package com.example.inventory_service.dto;

import com.example.inventory_service.model.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponseDTO {

    private UUID id;
    private String name;
    private String location;

    public static WarehouseResponseDTO fromEntity(Warehouse warehouse) {
        return WarehouseResponseDTO.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .build();
    }
}
