package com.example.inventory_service.repository;

import com.example.inventory_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query(value = "SELECT * FROM products p WHERE " +
           "(CAST(:category AS VARCHAR) IS NULL OR p.category = :category) AND " +
           "(CAST(:minPrice AS NUMERIC) IS NULL OR p.unit_price >= :minPrice) AND " +
           "(CAST(:maxPrice AS NUMERIC) IS NULL OR p.unit_price <= :maxPrice) AND " +
           "(CAST(:search AS VARCHAR) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))",
           countQuery = "SELECT COUNT(*) FROM products p WHERE " +
           "(CAST(:category AS VARCHAR) IS NULL OR p.category = :category) AND " +
           "(CAST(:minPrice AS NUMERIC) IS NULL OR p.unit_price >= :minPrice) AND " +
           "(CAST(:maxPrice AS NUMERIC) IS NULL OR p.unit_price <= :maxPrice) AND " +
           "(CAST(:search AS VARCHAR) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%')))",
           nativeQuery = true)
    Page<Product> findByFilters(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    Long countActiveProducts();
}
