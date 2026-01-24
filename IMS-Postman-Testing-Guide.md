# IMS Microservices – Postman API Testing Guide (Auth + Inventory + Order)

This guide helps you test the **Inventory Management System (IMS)** backend using the provided **Postman Collection**.

Services:
- **Auth Service** → `http://localhost:8081`
- **Inventory Service** → `http://localhost:8082`
- **Order Service** → `http://localhost:8083`

---

## 1) Prerequisites (Before Postman)

### 1.1 Start PostgreSQL
Make sure PostgreSQL is running and these databases exist:
- `auth_db`
- `inventory_db`
- `order_db`

### 1.2 Start all 3 Spring Boot services
Run each microservice on its port:
- Auth Service → **8081**
- Inventory Service → **8082**
- Order Service → **8083**

### 1.3 (Optional) Start Kafka (Bonus Flow)
If you implemented Kafka:
- Start Kafka + Zookeeper
- Ensure topics exist:
  - `stock.in.events`
  - `stock.out.events`
  - `low.stock.alerts`

> If Kafka is not running, **Purchase Receive / Sales Confirm** might fail or stock may not update automatically.

---

## 2) Import the Postman Collection

### 2.1 Save the collection JSON
Create a file:
- `IMS-Microservices.postman_collection.json`

Paste the JSON collection into it and save.

### 2.2 Import in Postman
Postman → **Import** → **File** → select the JSON file.

---

## 3) Collection Variables (Important)

The collection uses these variables:

| Variable | Purpose | Default |
|---------|---------|---------|
| `AUTH_BASE_URL` | Auth service base URL | `http://localhost:8081` |
| `INVENTORY_BASE_URL` | Inventory service base URL | `http://localhost:8082` |
| `ORDER_BASE_URL` | Order service base URL | `http://localhost:8083` |
| `JWT_TOKEN` | Stores JWT after login | empty |
| `productId` | Stores created product id | empty |
| `warehouseId` | Stores created warehouse id | empty |
| `purchaseOrderId` | Stores created purchase order id | empty |
| `salesOrderId` | Stores created sales order id | empty |

### 3.1 Where to view/edit variables
Postman → Collection → **Variables tab**

---

## 4) Step-by-Step API Testing Flow (Recommended Order)

Follow this exact order so IDs and JWT token are auto-saved.

---

# ✅ A) AUTH SERVICE TESTING

## A1) Register User
**Request:** `POST {{AUTH_BASE_URL}}/api/auth/register`

**Purpose:** Creates a user account.

**Body example:**
```json
{
  "name": "Test User",
  "email": "testuser@gmail.com",
  "password": "Test@123",
  "role": "ADMIN"
}
```

**Expected Response:**
- `201 Created` (or `200 OK` depending on implementation)

**Common Errors:**
- `409 Conflict` → email already exists
- `400 Bad Request` → validation failed

---

## A2) Login (Save JWT Token)
**Request:** `POST {{AUTH_BASE_URL}}/api/auth/login`

**Purpose:** Logs in and automatically saves JWT into `JWT_TOKEN`.

**Body example:**
```json
{
  "email": "testuser@gmail.com",
  "password": "Test@123"
}
```

**Expected Response:**
- `200 OK`
- Response contains token field like:
  - `token` OR `accessToken` OR `jwt`

**Automation:**
A Postman test script extracts token and saves it into:
- `JWT_TOKEN`

**If token is not being saved:**
- Check your login response JSON field name.
- Update the script:
```js
let token = jsonData.token;
pm.collectionVariables.set("JWT_TOKEN", token);
```

---

## A3) Get Current User (Me)
**Request:** `GET {{AUTH_BASE_URL}}/api/auth/me`

**Header:**
- `Authorization: Bearer {{JWT_TOKEN}}`

**Expected Response:**
- `200 OK`
- Returns current logged-in user info

**Common Errors:**
- `401 Unauthorized` → missing/invalid token

---

# ✅ B) INVENTORY SERVICE TESTING

## B1) Create Product
**Request:** `POST {{INVENTORY_BASE_URL}}/api/products`

**Header:**
- `Authorization: Bearer {{JWT_TOKEN}}`

**Body example:**
```json
{
  "sku": "SKU-1001",
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse",
  "category": "Electronics",
  "unitPrice": 599.99,
  "reorderLevel": 5
}
```

**Expected Response:**
- `201 Created`
- Response contains `id` (saved to `productId`)

**Common Errors:**
- `409 Conflict` → SKU already exists
- `403 Forbidden` → role not allowed

---

## B2) Get Product By ID
**Request:** `GET {{INVENTORY_BASE_URL}}/api/products/{{productId}}`

**Expected Response:**
- `200 OK`
- Returns product details

---

## B3) List Products (Pagination + Sorting + Filtering)
**Request:**
`GET {{INVENTORY_BASE_URL}}/api/products?page=0&size=10&sort=name,asc&category=Electronics`

**Expected Response:**
- `200 OK`
- Page-based response

---

## B4) Update Product
**Request:** `PUT {{INVENTORY_BASE_URL}}/api/products/{{productId}}`

**Body example:**
```json
{
  "name": "Wireless Mouse - Updated",
  "description": "Updated description",
  "category": "Electronics",
  "unitPrice": 649.99,
  "reorderLevel": 10
}
```

**Expected Response:**
- `200 OK`

---

## B5) Create Warehouse
**Request:** `POST {{INVENTORY_BASE_URL}}/api/warehouses`

**Body example:**
```json
{
  "name": "Main Warehouse",
  "location": "Delhi"
}
```

**Expected Response:**
- `201 Created`
- Response contains `id` (saved to `warehouseId`)

---

## B6) Adjust Stock (Manual)
**Request:** `POST {{INVENTORY_BASE_URL}}/api/inventory/adjust`

**Purpose:** Adds initial stock manually.

**Body example:**
```json
{
  "productId": "{{productId}}",
  "warehouseId": "{{warehouseId}}",
  "quantity": 50,
  "reason": "Initial stock"
}
```

**Expected Response:**
- `200 OK`

---

## B7) Get Inventory Stock
**Request:** `GET {{INVENTORY_BASE_URL}}/api/inventory?productId={{productId}}&warehouseId={{warehouseId}}`

**Expected Response:**
- `200 OK`
- Returns current quantity

---

## B8) Upload Product Image (File Upload)
**Request:** `POST {{INVENTORY_BASE_URL}}/api/products/{{productId}}/image`

**Body type:** `form-data`

Key:
- `file` → choose any image file

**Expected Response:**
- `200 OK` or `201 Created`

**Common Errors:**
- `415 Unsupported Media Type` → missing multipart config
- `400 Bad Request` → file missing

---

# ✅ C) ORDER SERVICE TESTING

## C1) Create Purchase Order
**Request:** `POST {{ORDER_BASE_URL}}/api/purchase-orders`

**Body example:**
```json
{
  "supplierName": "ABC Suppliers",
  "items": [
    {
      "productId": "{{productId}}",
      "warehouseId": "{{warehouseId}}",
      "quantity": 20,
      "unitCost": 450.00
    }
  ]
}
```

**Expected Response:**
- `201 Created`
- Response contains id saved to `purchaseOrderId`

---

## C2) Receive Purchase Order (Triggers STOCK_IN_EVENT)
**Request:** `PATCH {{ORDER_BASE_URL}}/api/purchase-orders/{{purchaseOrderId}}/receive`

**Expected Response:**
- `200 OK`

**Expected Stock Behavior:**
- Inventory should increase by +20 for that product+warehouse.
- If Kafka is enabled, Order publishes `STOCK_IN_EVENT`.

---

## C3) Create Sales Order
**Request:** `POST {{ORDER_BASE_URL}}/api/sales-orders`

**Body example:**
```json
{
  "customerName": "John Doe",
  "items": [
    {
      "productId": "{{productId}}",
      "warehouseId": "{{warehouseId}}",
      "quantity": 5,
      "unitPrice": 699.99
    }
  ]
}
```

**Expected Response:**
- `201 Created`
- id saved to `salesOrderId`

---

## C4) Confirm Sales Order (Triggers STOCK_OUT_EVENT)
**Request:** `PATCH {{ORDER_BASE_URL}}/api/sales-orders/{{salesOrderId}}/confirm`

**Expected Response:**
- `200 OK`

**Expected Stock Behavior:**
- Inventory should reduce by -5 for that product+warehouse.
- If Kafka is enabled, Order publishes `STOCK_OUT_EVENT`.

---

# ✅ D) ANALYTICS TESTING

## D1) Analytics - Summary
**Request:** `GET {{INVENTORY_BASE_URL}}/api/analytics/summary`

**Expected Response Example:**
```json
{
  "totalProducts": 1,
  "totalWarehouses": 1,
  "totalStockUnits": 65,
  "totalStockValue": 42249.35
}
```

---

## D2) Analytics - Low Stock
**Request:** `GET {{INVENTORY_BASE_URL}}/api/analytics/low-stock?threshold=5`

**Expected Response:**
- list of low-stock items

---

# ✅ E) Rate Limiting Testing (If enabled)

If your backend has rate limiting, you should test it:

1. Rapidly hit any endpoint (e.g. List Products) multiple times quickly.
2. After limit is exceeded, you should see:

**Expected Response:**
- `429 Too Many Requests`

---

## 5) JWT Token Usage Rules (Important)

All protected endpoints must include:

**Header**
```
Authorization: Bearer {{JWT_TOKEN}}
```

If you get `401`:
- run Login again
- check token saved in collection variables

---

## 6) Typical Error Codes You Should Handle

| Status | Meaning | Common Cause |
|-------:|---------|--------------|
| 400 | Bad Request | Validation errors |
| 401 | Unauthorized | Missing/invalid JWT |
| 403 | Forbidden | Role not allowed |
| 404 | Not Found | ID not present |
| 409 | Conflict | SKU/email already exists |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Server Error | Unexpected exception |

---

## 7) Troubleshooting

### 7.1 Token not saving
Fix the token field name in Postman script.

### 7.2 Inventory not updating after order receive/confirm
- Kafka not running
- Consumer not configured
- Events not being published

### 7.3 File upload failing
Ensure Inventory Service supports:
- `multipart/form-data`
- proper upload directory exists

### 7.4 403 Forbidden
Your user role is not allowed. Use ADMIN/MANAGER.

---

## 8) Recommended Testing Sequence (One-Liner)
1. Register → 2. Login → 3. Create Product → 4. Create Warehouse → 5. Adjust Stock → 6. Purchase Order → Receive → 7. Sales Order → Confirm → 8. Analytics

---

## 9) What to show in Demo Video (5–10 min)
- Login + JWT token saved
- Protected endpoint access
- Create product + warehouse
- File upload
- Purchase receive (stock IN)
- Sales confirm (stock OUT)
- Analytics summary
- Rate limiting (429)
- Email notification (low stock)

---

## 10) Notes for AI IDE / Implementation Alignment
If your backend endpoint names differ from this guide, update either:
- backend endpoint paths, or
- Postman request URLs

Keep response fields consistent for ID extraction:
- `id` for entity IDs
- `token` for JWT token
