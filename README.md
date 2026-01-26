# Inventory Management System (IMS)

A comprehensive microservices-based Inventory Management System built with Spring Boot, featuring authentication, inventory tracking, order management, and real-time analytics.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Running Services Individually](#running-services-individually)
- [Docker Deployment](#docker-deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

---

## 🎯 Overview

The Inventory Management System (IMS) is a distributed microservices application designed to manage products, warehouses, inventory stock, purchase orders, and sales orders. The system implements event-driven architecture using Apache Kafka for asynchronous communication between services.

### Key Capabilities

- **User Authentication & Authorization**: JWT-based authentication with role-based access control (RBAC)
- **Product Management**: CRUD operations for products with image upload support
- **Warehouse Management**: Multi-warehouse support with location tracking
- **Inventory Tracking**: Real-time stock management with movement history
- **Order Processing**: Purchase orders and sales orders with automatic stock updates
- **Analytics & Reporting**: Dashboard with summary statistics and low-stock alerts
- **Event-Driven Architecture**: Kafka-based event streaming for stock updates
- **Email Notifications**: Automated alerts for low stock situations

---

## 🏗️ Architecture

The system follows a **microservices architecture** with three main services:

```
┌─────────────────┐
│   Auth Service  │  Port: 8081
│  (JWT + Users)  │
└────────┬────────┘
         │
         │ JWT Token
         │
┌────────▼────────┐      ┌─────────────────┐
│Inventory Service│◄────►│  Order Service  │
│   Port: 8082    │ Kafka │   Port: 8083    │
└─────────────────┘      └─────────────────┘
         │                       │
         └───────────┬───────────┘
                     │
            ┌────────▼────────┐
            │   PostgreSQL    │
            │  (3 Databases)  │
            └─────────────────┘
```

### Service Responsibilities

1. **Auth Service** (`auth-service`)
   - User registration and authentication
   - JWT token generation and validation
   - Role management (ADMIN, MANAGER, USER)

2. **Inventory Service** (`inventory-service`)
   - Product CRUD operations
   - Warehouse management
   - Stock tracking and adjustments
   - Stock transfers between warehouses
   - Analytics and reporting
   - Low stock email alerts
   - Kafka consumer for stock events

3. **Order Service** (`order-service`)
   - Purchase order management
   - Sales order management
   - Kafka producer for stock events
   - Order status tracking

---

## ✨ Features

### Authentication & Security
- ✅ JWT-based authentication
- ✅ Role-based access control (RBAC)
- ✅ Password encryption
- ✅ Protected API endpoints
- ✅ Token expiration handling

### Product Management
- ✅ Create, read, update, delete products
- ✅ Product image upload
- ✅ SKU uniqueness validation
- ✅ Category-based filtering
- ✅ Pagination and sorting
- ✅ Search capabilities

### Inventory Management
- ✅ Multi-warehouse support
- ✅ Real-time stock tracking
- ✅ Manual stock adjustments
- ✅ Stock transfers between warehouses
- ✅ Stock movement history
- ✅ Low stock alerts via email
- ✅ Automatic stock updates via Kafka events

### Order Management
- ✅ Purchase order creation and receiving
- ✅ Sales order creation and confirmation
- ✅ Automatic stock updates on order events
- ✅ Order status tracking
- ✅ Multi-item order support

### Analytics & Reporting
- ✅ Dashboard summary (total products, warehouses, stock value)
- ✅ Low stock reports
- ✅ Stock movement history
- ✅ Configurable threshold alerts

### Performance & Scalability
- ✅ Caching with Caffeine
- ✅ Rate limiting with Bucket4j
- ✅ Database indexing
- ✅ Connection pooling
- ✅ Async event processing

---

## 🛠️ Tech Stack

### Backend
- **Java 21**
- **Spring Boot 4.0.2**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data persistence
- **Spring Kafka** - Event streaming
- **PostgreSQL** - Relational database
- **Apache Kafka** - Message broker
- **JWT (jjwt)** - Token management

### Tools & Libraries
- **Lombok** - Boilerplate reduction
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **Caffeine Cache** - In-memory caching
- **Bucket4j** - Rate limiting
- **Spring Mail** - Email notifications
- **Maven** - Build tool

### Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **Maven 3.6+**
- **PostgreSQL 13+** (if running locally)
- **Docker & Docker Compose** (for containerized deployment)
- **Apache Kafka** (if running locally, or use Docker)
- **Git** (for cloning the repository)

### Optional
- **Postman** (for API testing)
- **IntelliJ IDEA** or **Eclipse** (IDE)

---

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

The easiest way to run the entire system:

```bash
# Clone the repository
git clone <repository-url>
cd Inventory_Management_System

# Start all services (PostgreSQL, Kafka, Zookeeper, and all microservices)
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

**Services will be available at:**
- Auth Service: `http://localhost:8081`
- Inventory Service: `http://localhost:8082`
- Order Service: `http://localhost:8083`
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`

### Option 2: Local Development Setup

#### Step 1: Database Setup

```bash
# Start PostgreSQL (using Docker or local installation)
docker run -d \
  --name postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:13

# Create databases
psql -U postgres -h localhost
```

```sql
CREATE DATABASE auth_db;
CREATE DATABASE inventory_db;
CREATE DATABASE order_db;
\q
```

#### Step 2: Kafka Setup (Optional - for event-driven features)

```bash
# Using Docker Compose for Kafka
docker-compose up -d zookeeper kafka

# Or download and run Kafka locally
# https://kafka.apache.org/downloads
```

#### Step 3: Build and Run Services

**Terminal 1 - Auth Service:**
```bash
cd auth-service
mvn clean install
mvn spring-boot:run
```

**Terminal 2 - Inventory Service:**
```bash
cd inventory-service
mvn clean install
mvn spring-boot:run
```

**Terminal 3 - Order Service:**
```bash
cd order-service
mvn clean install
mvn spring-boot:run
```

---

## 📁 Project Structure

```
Inventory_Management_System/
├── auth-service/                 # Authentication microservice
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/auth_service/
│   │   │   │       ├── config/          # Security, OpenAPI config
│   │   │   │       ├── controller/      # REST controllers
│   │   │   │       ├── dto/            # Data Transfer Objects
│   │   │   │       ├── exception/       # Exception handlers
│   │   │   │       ├── model/          # Entity models
│   │   │   │       ├── repository/     # JPA repositories
│   │   │   │       ├── security/       # JWT utilities
│   │   │   │       └── service/        # Business logic
│   │   │   └── resources/
│   │   │       └── application.yaml    # Service configuration
│   │   └── test/                       # Unit tests
│   ├── Dockerfile
│   └── pom.xml
│
├── inventory-service/            # Inventory management microservice
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/inventory_service/
│   │   │   │       ├── config/          # Security, Kafka, Cache config
│   │   │   │       ├── controller/     # REST controllers
│   │   │   │       ├── dto/            # DTOs
│   │   │   │       ├── exception/       # Exception handlers
│   │   │   │       ├── kafka/          # Kafka consumers
│   │   │   │       ├── model/          # Entity models
│   │   │   │       ├── repository/     # JPA repositories
│   │   │   │       ├── security/       # JWT filter
│   │   │   │       └── service/        # Business logic
│   │   │   └── resources/
│   │   │       └── application.yaml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── order-service/                # Order management microservice
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/order_service/
│   │   │   │       ├── config/          # Security, Kafka config
│   │   │   │       ├── controller/     # REST controllers
│   │   │   │       ├── dto/            # DTOs
│   │   │   │       ├── exception/      # Exception handlers
│   │   │   │       ├── kafka/          # Kafka producers
│   │   │   │       ├── model/          # Entity models
│   │   │   │       ├── repository/    # JPA repositories
│   │   │   │       ├── security/       # JWT filter
│   │   │   │       └── service/       # Business logic
│   │   │   └── resources/
│   │   │       └── application.yaml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── docker-compose.yml            # Docker orchestration
├── init.sql                      # Database initialization script
├── test.json                     # Postman collection
├── IMS-Postman-Testing-Guide.md # API testing guide
└── README.md                     # This file
```

---

## ⚙️ Configuration

### Environment Variables

Each service can be configured using environment variables or `application.yaml` files.

#### Auth Service (`auth-service/src/main/resources/application.yaml`)

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: postgres
    password: postgres

jwt:
  secret: ${JWT_SECRET:mySecretKeyForJWTTokenGenerationWhichIsVeryLongAndSecure2024}
  expiration: ${JWT_EXPIRATION:86400000}  # 24 hours in milliseconds
```

#### Inventory Service (`inventory-service/src/main/resources/application.yaml`)

```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inventory_db
  kafka:
    bootstrap-servers: localhost:9092
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}

jwt:
  secret: ${JWT_SECRET:mySecretKeyForJWTTokenGenerationWhichIsVeryLongAndSecure2024}

file:
  upload-dir: ${FILE_UPLOAD_DIR:uploads/products}

alert:
  email:
    to: ${ALERT_EMAIL:admin@ims.com}
```

#### Order Service (`order-service/src/main/resources/application.yaml`)

```yaml
server:
  port: 8083

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/order_db
  kafka:
    bootstrap-servers: localhost:9092

jwt:
  secret: ${JWT_SECRET:mySecretKeyForJWTTokenGenerationWhichIsVeryLongAndSecure2024}
```

### Important Configuration Notes

1. **JWT Secret**: Use a strong, unique secret key in production. Set via `JWT_SECRET` environment variable.

2. **Database URLs**: Update database URLs if using different host/port.

3. **Kafka**: Ensure Kafka is running if using event-driven features. Set `KAFKA_ENABLED=true` in order-service.

4. **Email Configuration**: Configure SMTP settings in inventory-service for low stock alerts.

5. **File Upload**: Ensure the upload directory exists and has write permissions.

---

## 📚 API Documentation

### Swagger UI

Once services are running, access interactive API documentation:

- **Auth Service**: `http://localhost:8081/swagger-ui.html`
- **Inventory Service**: `http://localhost:8082/swagger-ui.html`
- **Order Service**: `http://localhost:8083/swagger-ui.html`

### API Endpoints Overview

#### Auth Service (`/api/auth`)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/me` - Get current user info

#### Inventory Service (`/api`)
- **Products**: `/api/products`
  - `GET /api/products` - List products (paginated, filtered, sorted)
  - `POST /api/products` - Create product
  - `GET /api/products/{id}` - Get product by ID
  - `PUT /api/products/{id}` - Update product
  - `DELETE /api/products/{id}` - Delete product
  - `POST /api/products/{id}/image` - Upload product image

- **Warehouses**: `/api/warehouses`
  - `GET /api/warehouses` - List warehouses
  - `POST /api/warehouses` - Create warehouse
  - `GET /api/warehouses/{id}` - Get warehouse by ID

- **Inventory**: `/api/inventory`
  - `GET /api/inventory` - Get all inventory (paginated)
  - `POST /api/inventory/adjust` - Adjust stock manually
  - `POST /api/inventory/transfer` - Transfer stock between warehouses
  - `GET /api/inventory/movements` - Get stock movement history

- **Analytics**: `/api/analytics`
  - `GET /api/analytics/summary` - Get dashboard summary
  - `GET /api/analytics/low-stock` - Get low stock items

#### Order Service (`/api`)
- **Purchase Orders**: `/api/purchase-orders`
  - `GET /api/purchase-orders` - List purchase orders
  - `POST /api/purchase-orders` - Create purchase order
  - `GET /api/purchase-orders/{id}` - Get purchase order by ID
  - `PATCH /api/purchase-orders/{id}/receive` - Receive purchase order (triggers stock IN)

- **Sales Orders**: `/api/sales-orders`
  - `GET /api/sales-orders` - List sales orders
  - `POST /api/sales-orders` - Create sales order
  - `GET /api/sales-orders/{id}` - Get sales order by ID
  - `PATCH /api/sales-orders/{id}/confirm` - Confirm sales order (triggers stock OUT)

### Authentication

Most endpoints require JWT authentication. Include the token in the request header:

```
Authorization: Bearer <your-jwt-token>
```

### Example API Flow

1. **Register User**
   ```bash
   POST http://localhost:8081/api/auth/register
   {
     "name": "Admin User",
     "email": "admin@example.com",
     "password": "SecurePass123!",
     "role": "ADMIN"
   }
   ```

2. **Login**
   ```bash
   POST http://localhost:8081/api/auth/login
   {
     "email": "admin@example.com",
     "password": "SecurePass123!"
   }
   ```
   Response includes `token` field - save this for subsequent requests.

3. **Create Product** (requires JWT)
   ```bash
   POST http://localhost:8082/api/products
   Authorization: Bearer <token>
   {
     "sku": "SKU-001",
     "name": "Laptop",
     "description": "High-performance laptop",
     "category": "Electronics",
     "unitPrice": 999.99,
     "reorderLevel": 10
   }
   ```

For detailed API testing instructions, see [IMS-Postman-Testing-Guide.md](./IMS-Postman-Testing-Guide.md).

---

## 🧪 Testing

### Postman Collection

Import the `test.json` file into Postman for comprehensive API testing. The collection includes:

- Pre-configured requests for all endpoints
- Automatic JWT token extraction and storage
- Test scripts with assertions
- Variable management for IDs and tokens

**Import Steps:**
1. Open Postman
2. Click **Import**
3. Select `test.json` file
4. Collection variables are pre-configured

**Recommended Testing Flow:**
1. Register User → 2. Login → 3. Create Product → 4. Create Warehouse → 5. Adjust Stock → 6. Create Purchase Order → 7. Receive Purchase Order → 8. Create Sales Order → 9. Confirm Sales Order → 10. Check Analytics

### Unit Tests

Run tests for each service:

```bash
# Auth Service
cd auth-service
mvn test

# Inventory Service
cd inventory-service
mvn test

# Order Service
cd order-service
mvn test
```

---

## 🔧 Running Services Individually

### Prerequisites Check

Before running services individually, ensure:

1. **PostgreSQL is running** with databases created:
   ```sql
   CREATE DATABASE auth_db;
   CREATE DATABASE inventory_db;
   CREATE DATABASE order_db;
   ```

2. **Kafka is running** (for event-driven features):
   ```bash
   # Using Docker
   docker-compose up -d zookeeper kafka
   ```

### Running Auth Service

```bash
cd auth-service
mvn spring-boot:run
# Service starts on http://localhost:8081
```

### Running Inventory Service

```bash
cd inventory-service
mvn spring-boot:run
# Service starts on http://localhost:8082
```

### Running Order Service

```bash
cd order-service
mvn spring-boot:run
# Service starts on http://localhost:8083
```

---

## 🐳 Docker Deployment

### Building Docker Images

```bash
# Build all services
docker-compose build

# Build specific service
docker-compose build auth-service
```

### Running with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Docker Compose Services

- `postgres` - PostgreSQL database (port 5432)
- `zookeeper` - Kafka Zookeeper (port 2181)
- `kafka` - Apache Kafka broker (port 9092)
- `auth-service` - Auth microservice (port 8081)
- `inventory-service` - Inventory microservice (port 8082)
- `order-service` - Order microservice (port 8083)

### Health Checks

Check service health:

```bash
# Check all containers
docker-compose ps

# Check specific service logs
docker-compose logs auth-service
docker-compose logs inventory-service
docker-compose logs order-service
```

---

## 🔍 Troubleshooting

### Common Issues

#### 1. Port Already in Use

**Error**: `Port 8081/8082/8083 already in use`

**Solution**:
```bash
# Find process using port
netstat -ano | findstr :8081  # Windows
lsof -i :8081                 # Linux/Mac

# Kill process or change port in application.yaml
```

#### 2. Database Connection Failed

**Error**: `Connection refused` or `Database not found`

**Solution**:
- Verify PostgreSQL is running: `docker ps` or `pg_isready`
- Check database exists: `psql -U postgres -l`
- Verify connection string in `application.yaml`
- Check network connectivity (if using Docker)

#### 3. Kafka Connection Failed

**Error**: `Bootstrap broker localhost:9092 not available`

**Solution**:
- Ensure Kafka is running: `docker-compose ps kafka`
- Check Kafka health: `docker-compose logs kafka`
- Verify `KAFKA_BOOTSTRAP_SERVERS` in application.yaml
- Wait for Kafka to fully start (may take 30-60 seconds)

#### 4. JWT Token Invalid

**Error**: `401 Unauthorized` or `Invalid token`

**Solution**:
- Ensure JWT secret matches across all services
- Check token expiration (default: 24 hours)
- Verify token format: `Bearer <token>`
- Re-login to get a new token

#### 5. File Upload Fails

**Error**: `415 Unsupported Media Type` or `File not found`

**Solution**:
- Ensure `multipart/form-data` is used
- Check file size limits in `application.yaml`
- Verify upload directory exists and has write permissions
- Check `file.upload-dir` configuration

#### 6. Stock Not Updating After Order Events

**Error**: Stock doesn't change after purchase/sales order

**Solution**:
- Verify Kafka is running and connected
- Check Kafka topics exist: `stock.in.events`, `stock.out.events`
- Review Kafka consumer logs: `docker-compose logs inventory-service`
- Ensure `KAFKA_ENABLED=true` in order-service

#### 7. Email Not Sending

**Error**: Low stock alerts not received

**Solution**:
- Configure SMTP settings in `inventory-service/application.yaml`
- Set `MAIL_USERNAME` and `MAIL_PASSWORD` environment variables
- For Gmail, use App Password (not regular password)
- Check email service logs

### Debug Mode

Enable debug logging:

```yaml
# In application.yaml
logging:
  level:
    com.example.auth_service: DEBUG
    org.springframework.security: DEBUG
    org.springframework.kafka: DEBUG
```

### Database Reset

To reset databases:

```bash
# Stop services
docker-compose down

# Remove volumes
docker-compose down -v

# Restart
docker-compose up -d
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/your-feature-name`
3. **Commit changes**: `git commit -m "Add your feature"`
4. **Push to branch**: `git push origin feature/your-feature-name`
5. **Submit a Pull Request**

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Write unit tests for new features
- Update documentation as needed

### Pull Request Checklist

- [ ] Code follows project style guidelines
- [ ] Tests pass locally
- [ ] Documentation updated
- [ ] No merge conflicts
- [ ] Commit messages are clear and descriptive

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Authors

- **Your Name** - *Initial work*

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Apache Kafka for event streaming capabilities
- PostgreSQL community for robust database support
- All open-source contributors

---

## 📞 Support

For issues, questions, or contributions:

- **Issues**: Open an issue on GitHub
- **Email**: [your-email@example.com]
- **Documentation**: Check [IMS-Postman-Testing-Guide.md](./IMS-Postman-Testing-Guide.md)

---

## 🔄 Version History

- **v1.0.0** - Initial release
  - Basic CRUD operations
  - JWT authentication
  - Kafka event streaming
  - Analytics dashboard

