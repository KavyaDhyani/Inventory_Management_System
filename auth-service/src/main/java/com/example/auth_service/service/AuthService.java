package com.example.auth_service.service;

import com.example.auth_service.dto.*;
import com.example.auth_service.exception.BadRequestException;
import com.example.auth_service.exception.ConflictException;
import com.example.auth_service.exception.ResourceNotFoundException;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.security.CustomUserDetails;
import com.example.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        // Determine role - use provided role or default to STAFF
        final Role.RoleName roleName;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                roleName = Role.RoleName.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid role. Valid roles are: ADMIN, MANAGER, STAFF");
            }
        } else {
            roleName = Role.RoleName.STAFF;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role " + roleName + " not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        user = userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponseDTO.of(token, UserResponseDTO.fromEntity(user));
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponseDTO.of(token, UserResponseDTO.fromEntity(userDetails.getUser()));
    }

    public UserResponseDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return UserResponseDTO.fromEntity(userDetails.getUser());
    }
}
