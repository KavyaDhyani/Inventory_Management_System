package com.example.auth_service.dto;

import com.example.auth_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDTO {

    private UUID id;
    private String name;

    public static RoleResponseDTO fromEntity(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName().name())
                .build();
    }
}
