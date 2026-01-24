package com.example.auth_service.controller;

import com.example.auth_service.dto.RoleAssignDTO;
import com.example.auth_service.dto.RoleResponseDTO;
import com.example.auth_service.dto.UserResponseDTO;
import com.example.auth_service.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Role management APIs (ADMIN only)")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all roles", description = "Returns all available roles in the system")
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign role to user", description = "Assigns a role to a user by email")
    public ResponseEntity<UserResponseDTO> assignRole(@Valid @RequestBody RoleAssignDTO request) {
        return ResponseEntity.ok(roleService.assignRoleToUser(request));
    }
}
