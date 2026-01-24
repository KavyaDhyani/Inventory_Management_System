package com.example.inventory_service.controller;

import com.example.inventory_service.dto.ProductCreateDTO;
import com.example.inventory_service.dto.ProductResponseDTO;
import com.example.inventory_service.dto.ProductUpdateDTO;
import com.example.inventory_service.model.ProductImage;
import com.example.inventory_service.service.FileStorageService;
import com.example.inventory_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductCreateDTO dto) {
        return new ResponseEntity<>(productService.createProduct(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    @Operation(summary = "Get all products with pagination and filtering")
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(category, minPrice, maxPrice, search, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update a product")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Upload product image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        ProductImage image = fileStorageService.uploadProductImage(id, file);
        return ResponseEntity.ok(Map.of(
                "imageId", image.getId(),
                "fileName", image.getFileName(),
                "message", "Image uploaded successfully"
        ));
    }

    @GetMapping("/{productId}/images/{imageId}")
    @Operation(summary = "Get product image")
    public ResponseEntity<Resource> getImage(@PathVariable UUID productId, @PathVariable UUID imageId) {
        Resource resource = fileStorageService.loadFileAsResource(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete product image")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID productId, @PathVariable UUID imageId) {
        fileStorageService.deleteProductImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
