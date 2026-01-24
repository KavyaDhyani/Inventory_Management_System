package com.example.inventory_service.service;

import com.example.inventory_service.exception.BadRequestException;
import com.example.inventory_service.exception.ResourceNotFoundException;
import com.example.inventory_service.model.Product;
import com.example.inventory_service.model.ProductImage;
import com.example.inventory_service.repository.ProductImageRepository;
import com.example.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads/products}")
    private String uploadDir;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Transactional
    public ProductImage uploadProductImage(UUID productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        validateFile(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String newFilename = productId + "_" + UUID.randomUUID() + "." + extension;

        try {
            Path targetLocation = Paths.get(uploadDir).resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            ProductImage image = ProductImage.builder()
                    .product(product)
                    .fileName(originalFilename)
                    .filePath(targetLocation.toString())
                    .build();

            image = productImageRepository.save(image);
            log.info("Uploaded image {} for product {}", newFilename, productId);
            return image;

        } catch (IOException e) {
            throw new BadRequestException("Failed to store file: " + e.getMessage());
        }
    }

    public Resource loadFileAsResource(UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        try {
            Path filePath = Paths.get(image.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + image.getFileName());
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File not found: " + image.getFileName());
        }
    }

    @Transactional
    public void deleteProductImage(UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        try {
            Path filePath = Paths.get(image.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", image.getFilePath(), e);
        }

        productImageRepository.delete(image);
        log.info("Deleted image with ID: {}", imageId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum allowed size (5MB)");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new BadRequestException("Filename is required");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("File type not allowed. Allowed types: " + ALLOWED_EXTENSIONS);
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
