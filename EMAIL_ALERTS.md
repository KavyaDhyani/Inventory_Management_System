# Email Alerts – Low Stock

This document describes how low-stock email alerts work in the **inventory-service**.

---

## 1. Configuration (`application.yaml`)

```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
    enabled: ${MAIL_ENABLED:false}   # Disabled by default
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

alert:
  email:
    to: ${ALERT_EMAIL:admin@ims.com}
```

---

## 2. Trigger Points

`checkLowStock()` runs after stock **decreases** in these flows:

| Operation | Method | What is checked |
|-----------|--------|----------------|
| Stock adjustment | `adjustStock()` | Stock at the adjusted warehouse |
| Stock transfer | `transferStock()` | **Source** warehouse (after quantity is reduced) |
| Stock OUT | `processStockOut()` | Warehouse when a sales order is confirmed |

---

## 3. Flow

```
Stock decreases
    → checkLowStock(stock, product)
        → if quantity <= reorderLevel
            → emailService.sendLowStockAlert(product, stock)
```

- `reorderLevel` comes from `Product`.
- Alert is sent only when `quantity <= product.getReorderLevel()`.

---

## 4. Email Features

| Feature | Implementation |
|---------|----------------|
| **@Async** | `sendLowStockAlert()` is `@Async`; `@EnableAsync` is in `AsyncConfig`. Sending is non-blocking. |
| **Enabled flag** | If `spring.mail.enabled=false`, the service logs that an alert would have been sent and returns without calling `mailSender.send()`. |
| **Error handling** | `try/catch` in `sendLowStockAlert()`; failures are logged and do not affect the main flow. |

---

## 5. Enabling Email Alerts

Set these environment variables (or equivalent in your deployment config):

```bash
export MAIL_ENABLED=true
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export ALERT_EMAIL=admin@yourcompany.com
```

**Gmail**

- Use an **App Password**, not your normal account password.
- App Passwords require 2FA to be enabled:  
  [Google App Passwords](https://support.google.com/accounts/answer/185833).

---

## 6. Relevant Code

| Component | Location |
|-----------|----------|
| `EmailService` | `inventory-service/.../service/EmailService.java` |
| `checkLowStock()` | `inventory-service/.../service/InventoryService.java` |
| `AsyncConfig` | `inventory-service/.../config/AsyncConfig.java` |
| Mail & alert config | `inventory-service/src/main/resources/application.yaml` |
