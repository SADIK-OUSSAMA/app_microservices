# E-Commerce Microservices Platform

A complete microservices-based e-commerce system built with Spring Boot, Spring Cloud, KeyCloak, Kafka, and Angular.

## Architecture Overview

The platform follows a modern microservices architecture with service discovery, centralized configuration, API gateway, event-driven communication, and OAuth2 authentication.

![Architecture Diagram](screenshots/Architecture.jpg)

## Tech Stack

- **Backend**: Spring Boot 3.x, Spring Cloud
- **Authentication**: KeyCloak (OAuth2/OIDC)
- **Service Discovery**: Eureka  
- **API Gateway**: Spring Cloud Gateway
- **Event Streaming**: Apache Kafka
- **Frontend**: Angular 19
- **AI Integration**: Telegram Bot with RAG (Retrieval-Augmented Generation)
- **Databases**: H2 (dev), PostgreSQL-ready

---

## Services

### Infrastructure Services

**Discovery Service (Eureka)** - Port 8761  
Service registry where all microservices register themselves for dynamic discovery.

![Eureka Dashboard](screenshots/eureka.png)

**Config Service** - Port 9999  
Centralized configuration management using Git repository.

![Config Server](screenshots/config.png)
![Git Configuration](screenshots/config-git.png)

**Gateway Service** - Port 8888  
API Gateway with JWT validation and intelligent routing to microservices.

### Business Services

**Customer Service** - Port 8081  
Manages customer data with REST API and H2 database.

![Customer Service API](screenshots/CUSTOMER5SERVICE.png)
![Customer Database](screenshots/customers-h2db.png)
![Customer API Endpoints](screenshots/customersapi.png)

**Inventory Service** - Port 8082  
Product catalog management with CRUD operations.

![Inventory Service](screenshots/INVENTORY-SERVICE.png)
![Products API](screenshots/productsapi.png)

**Billing Service** - Port 8083  
Invoice generation consuming data from Customer and Inventory services. Publishes billing events to Kafka.

![Billing Service](screenshots/BILLING-SERVICE.png)
![Bills View](screenshots/bills.png)

**Kafka Analytics Service** - Port 8085  
Event processing service with:
- Supplier management API
- Data analytics dashboard
- Kafka consumers for billing and supplier events

### AI & Bot Services

**Agent Bot** - Port 8087  
Telegram bot with RAG capabilities for document-based Q&A using OpenAI and Spring AI.

![Telegram Bot](screenshots/telegrambot.png)

**MCP Server** - Port 8989  
Exposes microservices tools via Model Context Protocol for AI agent integration.

---

## Authentication

KeyCloak manages authentication with realm-based multi-tenancy and role-based access control.

![KeyCloak Realm](screenshots/keycloak-realm.png)
![KeyCloak Client](screenshots/keycloak-client.png)

---

## Frontend

Angular 19 application with:
- Customer management
- Product catalog
- Billing dashboard
- KeyCloak integration (login/logout)

![Frontend 1](screenshots/front1.png)
![Frontend 2](screenshots/front2.png)
![Frontend 3](screenshots/front3.png)
![Frontend 4](screenshots/front4.png)

---

## Monitoring

Actuator endpoints provide health checks and metrics for all services.

![Actuator Health](screenshots/actuator%20health.png)

---






## API Endpoints

| Service | Base URL |
|---------|----------|
| Gateway | http://localhost:8888 |
| Customers | http://localhost:8888/CUSTOMER-SERVICE/api/customers |
| Products | http://localhost:8888/INVENTORY-SERVICE/api/products |
| Bills | http://localhost:8888/BILLING-SERVICE/api/bills |
| Suppliers | http://localhost:8085/api/suppliers |
| Analytics | http://localhost:8085/api/analytics |

---

## Event Streaming

Services communicate asynchronously via Kafka topics:
- `BILLING_EVENTS` - Published by Billing Service
- `SUPPLIER_EVENTS` - Published by Supplier Service  
- Consumed by Analytics Service for real-time reporting

