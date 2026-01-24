package com.example.inventory_service.repository;

import com.example.inventory_service.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    Optional<Warehouse> findByName(String name);

    boolean existsByName(String name);
}
