package com.example.auth_service.repository;

import com.example.auth_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(Role.RoleName name);

    boolean existsByName(Role.RoleName name);
}
