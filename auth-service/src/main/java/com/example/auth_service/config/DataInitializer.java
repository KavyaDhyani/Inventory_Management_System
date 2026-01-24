package com.example.auth_service.config;

import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initAdminUser();
    }

    private void initRoles() {
        Arrays.stream(Role.RoleName.values()).forEach(roleName -> {
            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .build();
                roleRepository.save(role);
                log.info("Created role: {}", roleName);
            }
        });
    }

    private void initAdminUser() {
        String adminEmail = "admin@ims.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User admin = User.builder()
                    .name("System Admin")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .roles(roles)
                    .build();

            userRepository.save(admin);
            log.info("Created default admin user: {}", adminEmail);
        }
    }
}
