# Customer Management API

A Spring Boot REST API that provides CRUD operations for managing customers. It supports
**two interchangeable data access layers** — raw **JDBC** and **Spring Data JPA** — selected at
runtime via a Spring qualifier, and uses **Flyway** for schema migrations against **PostgreSQL**.

## Table of Contents

1. [Overview](#overview)
2. [Tech Stack](#tech-stack)
3. [Prerequisites](#prerequisites)
4. [Getting Started](#getting-started)
5. [Configuration](#configuration)
6. [API Endpoints](#api-endpoints)
7. [Project Structure](#project-structure)
8. [Testing](#testing)
9. [Building & Deployment](#building--deployment)
10. [Further Documentation](#further-documentation)

## Overview

The Customer Management API lets you add, retrieve, update, and delete customer records. Each
customer has a `name`, `email`, and `age`. The application demonstrates a clean layered
architecture (controller → service → DAO) with:

- A `CustomerDao` interface backed by **two implementations** (`jdbc` and `jpa`), swappable via
  `@Qualifier`.
- Custom exceptions mapped to HTTP status codes (404, 409, 400).
- Flyway-managed database migrations.
- Containerized builds (Jib) and CI on GitHub Actions with Testcontainers.

> **Note:** This API currently has **no authentication** — all endpoints are publicly accessible.

## Tech Stack

| Category        | Technology                                            |
|-----------------|-------------------------------------------------------|
| Language        | Java 17                                               |
| Framework       | Spring Boot 3.2.5                                      |
| Web             | Spring Web (Spring MVC, embedded Tomcat)              |
| Data Access     | Spring Data JPA (Hibernate) **and** Spring JDBC       |
| Database        | PostgreSQL 14                                          |
| Migrations      | Flyway                                                 |
| Connection Pool | HikariCP                                               |
| Testing         | JUnit 5, Mockito, Testcontainers, WebTestClient, JavaFaker |
| Build           | Maven                                                  |
| Container Image | Jib (Amazon Corretto 17 base, multi-arch arm64/amd64) |
| CI/CD           | GitHub Actions, AWS Elastic Beanstalk (Dockerrun)     |

## Prerequisites

- **Java 17** or higher (JDK)
- **Maven 3.6+** (or use the bundled `./mvnw` wrapper)
- **Docker** & Docker Compose (for PostgreSQL and integration tests)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/avinaashghansam/Spring-Boot-Application.git
cd Spring-Boot-Application
```

### 2. Start PostgreSQL

A `docker-compose.yml` is provided at the repo root:

```bash
docker compose up -d db
```

This starts PostgreSQL 14 on `localhost:5432` with user `avinaash` / password `password`.

> The application connects to a database named `customer`. Create it once if it does not exist:
> ```bash
> docker exec -it postgres createdb -U avinaash customer
> ```

### 3. Run the application

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on **http://localhost:8080**. Flyway automatically applies the migrations in
`backend/src/main/resources/db/migration` on startup.

### 4. Try it out

```bash
# Create a customer
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Ada Lovelace","email":"ada@example.com","age":36}'

# List all customers
curl http://localhost:8080/api/v1/customers
```

## Configuration

Configuration lives in `backend/src/main/resources/application.yaml`.

| Property                                | Default                                       |
|-----------------------------------------|-----------------------------------------------|
| `server.port`                           | `8080`                                        |
| `spring.datasource.url`                 | `jdbc:postgresql://localhost:5432/customer`   |
| `spring.datasource.username`            | `avinaash`                                     |
| `spring.datasource.password`            | `password`                                     |
| `spring.jpa.hibernate.ddl-auto`         | `validate` (schema is owned by Flyway)        |
| Hikari `maximum-pool-size`              | `2`                                            |

Override any property via environment variables, e.g. the Docker/Beanstalk deployments set
`SPRING_DATASOURCE_URL`.

**Choosing the data access layer:** `CustomerService` injects the DAO with
`@Qualifier("jdbc")` by default. Switch to `@Qualifier("jpa")` in
`CustomerService.java` to use the JPA implementation instead.

## API Endpoints

Base path: `/api/v1/customers`

| Method   | Path                      | Description              | Request Body                  | Success Status |
|----------|---------------------------|--------------------------|-------------------------------|----------------|
| `GET`    | `/api/v1/customers`       | List all customers       | —                             | `200 OK`       |
| `GET`    | `/api/v1/customers/{id}`  | Get a customer by ID     | —                             | `200 OK`       |
| `POST`   | `/api/v1/customers`       | Register a new customer  | `CustomerRegistrationRequest` | `200 OK`       |
| `PUT`    | `/api/v1/customers/{id}`  | Update a customer        | `CustomerUpdateRequest`       | `200 OK`       |
| `DELETE` | `/api/v1/customers/{id}`  | Delete a customer by ID  | —                             | `200 OK`       |

**Request payloads**

```jsonc
// CustomerRegistrationRequest — all fields required
{ "name": "Ada Lovelace", "email": "ada@example.com", "age": 36 }

// CustomerUpdateRequest — all fields optional (partial update)
{ "email": "ada.lovelace@example.com" }
```

**Error responses**

| Scenario                          | Exception                     | HTTP Status        |
|-----------------------------------|-------------------------------|--------------------|
| Customer ID not found             | `ResourceNotFoundException`   | `404 Not Found`    |
| Email already registered          | `DuplicateResourceException`  | `409 Conflict`     |
| Update request with no changes    | `RequestValidationException`  | `400 Bad Request`  |

## Project Structure

```
Spring-Boot-Application/
├── docker-compose.yml              # PostgreSQL + customer-api services
├── README.md
├── docs/
│   └── TECHNICAL.md                # Architecture & design deep-dive
└── backend/
    ├── pom.xml                     # Maven build, Jib, Failsafe/Surefire
    ├── Dockerrun.aws.json          # AWS Elastic Beanstalk config
    ├── mvnw / mvnw.cmd             # Maven wrapper
    └── src/
        ├── main/
        │   ├── java/com/ag/
        │   │   ├── CustomersApplication.java
        │   │   ├── customer/
        │   │   │   ├── Customer.java            # JPA entity
        │   │   │   ├── controller/              # REST controller
        │   │   │   ├── service/                 # Business logic + JDBC/JPA DAOs
        │   │   │   └── repository/              # CustomerDao, JpaRepository, RowMapper
        │   │   ├── dto/                          # Request records
        │   │   └── exceptions/                   # Custom HTTP-mapped exceptions
        │   └── resources/
        │       ├── application.yaml
        │       └── db/migration/                 # Flyway V1, V2 SQL scripts
        └── test/                                 # Unit + integration tests
```

See [docs/TECHNICAL.md](docs/TECHNICAL.md) for a full architectural breakdown.

## Testing

Tests are split into **unit tests** (Surefire) and **integration tests** (Failsafe,
suffixed `*IT` / `*IntegrationTest`). Integration tests spin up a real PostgreSQL via
Testcontainers, so **Docker must be running**.

```bash
cd backend

# Unit tests only
./mvnw test

# Full build incl. integration tests
./mvnw verify
```

## Building & Deployment

**Build a container image** (no Dockerfile needed — uses Jib):

```bash
cd backend
./mvnw compile jib:build      # pushes docker.io/avinaashghansam/customer-api:<version> + :latest
```

**Run the whole stack** (API + database) with Compose:

```bash
docker compose up
```

**CI:** `.github/workflows/backend-ci.yaml` runs `mvn -ntp -B verify` on pull requests touching
`backend/**`, against a PostgreSQL 14 service container.

**Deployment:** `backend/Dockerrun.aws.json` configures AWS Elastic Beanstalk to run the
published image (port 80 → 8080, 512 MB). Set `SPRING_DATASOURCE_URL` to your RDS/Postgres endpoint.

## Further Documentation

- **[docs/TECHNICAL.md](docs/TECHNICAL.md)** — architecture, layering, data model, design decisions,
  and request lifecycle.
