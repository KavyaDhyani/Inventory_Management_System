package com.example.inventory_service.service;

import com.example.inventory_service.dto.*;
import com.example.inventory_service.exception.ConflictException;
import com.example.inventory_service.exception.ResourceNotFoundException;
import com.example.inventory_service.model.Product;
import com.example.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new ConflictException("Product with SKU " + dto.getSku() + " already exists");
        }

        Product product = Product.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .unitPrice(dto.getUnitPrice())
                .reorderLevel(dto.getReorderLevel() != null ? dto.getReorderLevel() : 10)
                .isActive(true)
                .build();

        product = productRepository.save(product);
        log.info("Created product with ID: {}", product.getId());
        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional(readOnly = true)
    public Product getProductEntityById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String search,
            Pageable pageable) {
        return productRepository.findByFilters(category, minPrice, maxPrice, search, pageable)
                .map(ProductResponseDTO::fromEntity);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductResponseDTO updateProduct(UUID id, ProductUpdateDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getCategory() != null) {
            product.setCategory(dto.getCategory());
        }
        if (dto.getUnitPrice() != null) {
            product.setUnitPrice(dto.getUnitPrice());
        }
        if (dto.getReorderLevel() != null) {
            product.setReorderLevel(dto.getReorderLevel());
        }
        if (dto.getIsActive() != null) {
            product.setIsActive(dto.getIsActive());
        }

        product = productRepository.save(product);
        log.info("Updated product with ID: {}", product.getId());
        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Deleted product with ID: {}", id);
    }
}
