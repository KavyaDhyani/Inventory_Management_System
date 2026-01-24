package com.example.auth_service.service;

import com.example.auth_service.dto.RoleAssignDTO;
import com.example.auth_service.dto.RoleResponseDTO;
import com.example.auth_service.dto.UserResponseDTO;
import com.example.auth_service.exception.BadRequestException;
import com.example.auth_service.exception.ResourceNotFoundException;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO assignRoleToUser(RoleAssignDTO request) {
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getUserEmail()));

        Role.RoleName roleName;
        try {
            roleName = Role.RoleName.valueOf(request.getRoleName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role name: " + request.getRoleName());
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRoleName()));

        user.getRoles().add(role);
        user = userRepository.save(user);

        return UserResponseDTO.fromEntity(user);
    }
}
