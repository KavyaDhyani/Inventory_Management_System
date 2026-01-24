package com.example.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleAssignDTO {

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotBlank(message = "Role name is required")
    private String roleName;
}
