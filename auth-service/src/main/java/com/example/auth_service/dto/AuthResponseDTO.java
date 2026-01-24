package com.example.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;
    private String tokenType;
    private UserResponseDTO user;

    public static AuthResponseDTO of(String token, UserResponseDTO user) {
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .user(user)
                .build();
    }
}
