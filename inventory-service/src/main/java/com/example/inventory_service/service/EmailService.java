package com.example.inventory_service.service;

import com.example.inventory_service.model.InventoryStock;
import com.example.inventory_service.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@ims.com}")
    private String fromEmail;

    @Value("${alert.email.to:admin@ims.com}")
    private String alertEmailTo;

    @Value("${spring.mail.enabled:false}")
    private boolean emailEnabled;

    @Async
    public void sendLowStockAlert(Product product, InventoryStock stock) {
        if (!emailEnabled) {
            log.info("Email disabled. Low stock alert for product {} would be sent to {}",
                    product.getSku(), alertEmailTo);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(alertEmailTo);
            message.setSubject("Low Stock Alert: " + product.getName());
            message.setText(buildLowStockEmailBody(product, stock));

            mailSender.send(message);
            log.info("Low stock alert email sent for product: {}", product.getSku());
        } catch (Exception e) {
            log.error("Failed to send low stock alert email for product: {}", product.getSku(), e);
        }
    }

    private String buildLowStockEmailBody(Product product, InventoryStock stock) {
        return String.format("""
                LOW STOCK ALERT

                Product Details:
                - SKU: %s
                - Name: %s
                - Category: %s

                Stock Information:
                - Warehouse: %s
                - Current Quantity: %d
                - Reorder Level: %d

                Please take action to replenish stock.

                ---
                Inventory Management System
                """,
                product.getSku(),
                product.getName(),
                product.getCategory() != null ? product.getCategory() : "N/A",
                stock.getWarehouse().getName(),
                stock.getQuantity(),
                product.getReorderLevel()
        );
    }
}
