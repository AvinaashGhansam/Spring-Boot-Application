# Technical Documentation — Customer Management API

This document describes the architecture, design decisions, data model, and request lifecycle of
the Customer Management API. For setup and usage, see the [README](../README.md).

---

## 1. System Overview

The application is a single Spring Boot 3.2.5 service exposing a REST API for customer CRUD
operations, backed by PostgreSQL. It is intentionally structured to demonstrate a clean,
layered architecture and a **swappable persistence layer** (raw JDBC vs. Spring Data JPA) behind a
single interface.

```
                 HTTP (JSON)
                     │
            ┌────────▼─────────┐
            │ CustomerController│   @RestController  — /api/v1/customers
            └────────┬─────────┘
                     │
            ┌────────▼─────────┐
            │  CustomerService │   business rules, validation, exception throwing
            └────────┬─────────┘
                     │  CustomerDao (interface)
        ┌────────────┴─────────────┐
        ▼                          ▼
┌───────────────┐         ┌────────────────┐
│ JDBC DAO      │         │ JPA DAO        │
│ @Qualifier    │         │ @Qualifier     │
│  ("jdbc")     │         │  ("jpa")       │
│ JdbcTemplate  │         │ CustomerRepo   │
└───────┬───────┘         └───────┬────────┘
        └────────────┬────────────┘
                     ▼
              ┌─────────────┐
              │ PostgreSQL  │  (schema managed by Flyway)
              └─────────────┘
```

---

## 2. Technology Choices

| Concern          | Choice                  | Rationale                                                        |
|------------------|-------------------------|------------------------------------------------------------------|
| Runtime          | Java 17                 | LTS; records, sealed types, modern language features.            |
| Framework        | Spring Boot 3.2.5       | Auto-configuration, embedded Tomcat, mature ecosystem.           |
| Persistence      | JPA **and** JDBC        | Demonstrates trade-offs; both satisfy one `CustomerDao` contract.|
| Database         | PostgreSQL 14           | Production-grade RDBMS; matches CI and deployment.               |
| Schema migration | Flyway                  | Versioned, repeatable, source-controlled DDL.                    |
| Pooling          | HikariCP                | Spring Boot default; fast, reliable.                             |
| Image build      | Jib                     | Dockerfile-less, reproducible, multi-arch images.                |
| Tests            | JUnit 5 + Testcontainers| Real-Postgres integration tests without manual setup.            |

---

## 3. Layered Architecture

### 3.1 Controller layer — `com.ag.customer.controller`

`CustomerController` is a thin `@RestController` mapped to `/api/v1/customers`. It performs no
business logic — it delegates directly to `CustomerService`. Write operations (`POST`, `PUT`,
`DELETE`) return `void`, so a successful call yields `200 OK` with an empty body.

### 3.2 Service layer — `com.ag.customer.service`

`CustomerService` holds all business rules and validation. It depends on the `CustomerDao`
abstraction, not a concrete implementation. Key responsibilities:

- **`getAllCustomers()`** — returns every customer.
- **`getCustomerById(id)`** — throws `ResourceNotFoundException` if absent.
- **`addCustomer(request)`** — rejects duplicate emails with `DuplicateResourceException`.
- **`updateCustomer(id, request)`** — partial update; validates the customer exists, that the new
  email is not taken, and that **at least one field actually changed** (otherwise
  `RequestValidationException`).
- **`deleteCustomerById(id)`** — verifies existence before deleting.

### 3.3 Data access layer — `com.ag.customer.repository` & `...service`

A single contract, two implementations:

**`CustomerDao` (interface)**

```java
List<Customer> selectAllCustomers();
Optional<Customer> selectCustomerById(Integer id);
void insertCustomer(Customer customer);
boolean existsPersonWithEmail(String email);
void deleteCustomerById(Integer id);
boolean existsPersonById(Integer id);
void updateCustomer(Customer customer);
```

| Implementation                    | Bean qualifier | Mechanism                                                       |
|-----------------------------------|----------------|----------------------------------------------------------------|
| `CustomerJDBCDataAccessService`   | `@Repository("jdbc")` | `JdbcTemplate` with parameterized SQL + `CustomerRowMapper`. |
| `CustomerJPADataAccessService`    | `@Repository("jpa")`  | Delegates to `CustomerRepository` (`JpaRepository`).         |

`CustomerService` selects one at construction time via `@Qualifier`. Today `"jdbc"` is wired in;
switching to `"jpa"` is a one-line change. This is effectively the **Strategy pattern** applied to
persistence.

**`CustomerRepository`** extends `JpaRepository<Customer, Integer>` and adds derived queries
`existsCustomerByEmail(String)` and `existsCustomerById(Integer)`.

**`CustomerRowMapper`** (`@Component`) implements Spring's `RowMapper<Customer>`, mapping the
`id`, `name`, `email`, `age` columns of a `ResultSet` into a `Customer`.

---

## 4. Data Model

### 4.1 Entity — `Customer`

| Field   | Type      | Constraints                          |
|---------|-----------|--------------------------------------|
| `id`    | `Integer` | Primary key, generated from sequence `customer_id_seq` (allocationSize 1) |
| `name`  | `String`  | `NOT NULL`                           |
| `email` | `String`  | `NOT NULL`, **unique**               |
| `age`   | `Integer` | `NOT NULL`                           |

Hibernate runs with `ddl-auto: validate` — it never creates or alters tables; the schema is owned
entirely by Flyway, and Hibernate only verifies that the mapping matches.

### 4.2 DTOs — `com.ag.dto`

Both are Java **records** (immutable):

- `CustomerRegistrationRequest(String name, String email, Integer age)` — all fields expected.
- `CustomerUpdateRequest(String name, String email, Integer age)` — any subset; nulls mean
  "leave unchanged."

Using DTOs decouples the wire format from the persistence entity.

### 4.3 Flyway migrations — `resources/db/migration`

| Version | File                          | Effect                                                        |
|---------|-------------------------------|---------------------------------------------------------------|
| V1      | `V1__Initial_Setup.sql`       | Creates `customer` table (`id BIGSERIAL PK`, `name`, `email UNIQUE`, `age`). |
| V2      | `V2__Change_Id_Column_Type.sql` | Converts `id` from `BIGSERIAL` to `INTEGER`.                |

Migrations run automatically on application startup.

---

## 5. Exception Handling

Custom exceptions extend `RuntimeException` and carry a `@ResponseStatus`, so Spring MVC maps them
to HTTP codes automatically — no explicit `@ExceptionHandler` needed.

| Exception                     | HTTP Status       | Thrown when                              |
|-------------------------------|-------------------|------------------------------------------|
| `ResourceNotFoundException`   | `404 Not Found`   | Looking up / deleting a missing customer |
| `DuplicateResourceException`  | `409 Conflict`    | Registering/updating to an existing email|
| `RequestValidationException`  | `400 Bad Request` | Update request contains no actual changes|

Error responses include the message (`server.error.include-message: always`) but **never** the
stack trace (`include-stacktrace: never`).

---

## 6. Request Lifecycle (example: `POST /api/v1/customers`)

1. **Controller** receives the JSON body, deserialized into a `CustomerRegistrationRequest`.
2. **Service** (`addCustomer`) calls `dao.existsPersonWithEmail(email)`.
   - If `true` → throws `DuplicateResourceException` → `409`.
3. Otherwise it constructs a `Customer` and calls `dao.insertCustomer(...)`.
4. The active DAO (JDBC by default) executes an `INSERT` against PostgreSQL.
5. Controller method returns `void` → `200 OK`, empty body.

---

## 7. Configuration Reference

From `application.yaml`:

- **Server:** port `8080`; error responses include message, exclude stack trace.
- **Datasource:** `jdbc:postgresql://localhost:5432/customer`, user `avinaash`.
- **HikariCP:** max pool size `2`, idle timeout `10m`, max lifetime `30m`, connection timeout `30s`.
- **JPA/Hibernate:** `ddl-auto: validate`, SQL formatting on, `show-sql: false`.
- **App type:** `servlet` (Spring MVC, not reactive — WebFlux is test-scope only, used by
  `WebTestClient` in integration tests).

Properties are overridable by environment variable (e.g. `SPRING_DATASOURCE_URL` in Docker/AWS).

---

## 8. Testing Strategy

| Suite               | Runner    | Naming                         | Notes                                              |
|---------------------|-----------|--------------------------------|----------------------------------------------------|
| Unit tests          | Surefire  | everything except `*IT`/`*IntegrationTest` | Mockito for collaborators.            |
| Integration tests   | Failsafe  | `*IT.java`, `*IntegrationTest.java`        | Real PostgreSQL via Testcontainers.   |

Notable tests:

- `CustomerServiceTest` — business logic with mocked DAO.
- `CustomerJDBCDataAccessServiceTest`, `CustomerJPADataAccessServiceTest`,
  `CustomerRepositoryTest`, `CustomerRowMapperTest` — persistence layer.
- `journey/CustomerIntegrationTest` — full HTTP round-trip using `WebTestClient`.
- `AbstractTestContainersUnitTest` / `TestContainersTest` — shared Testcontainers + JavaFaker setup.

The build reserves a random Tomcat port (`build-helper-maven-plugin`) and starts/stops the app
around integration tests via the Spring Boot Maven plugin.

---

## 9. Build, CI & Deployment

- **Image build:** Jib (`jib-maven-plugin`) produces a multi-arch (arm64/amd64) image on an
  `amazoncorretto:17` base, tagged `docker.io/avinaashghansam/customer-api:<version>` and `:latest`.
  No Dockerfile is required.
- **CI:** `.github/workflows/backend-ci.yaml` triggers on PRs touching `backend/**`, runs
  `mvn -ntp -B verify` on Ubuntu with a PostgreSQL 14 service container and Temurin JDK 17 (Maven
  cache enabled).
- **Deployment:** `backend/Dockerrun.aws.json` (AWS Elastic Beanstalk v2) runs the published image,
  maps host port 80 → container 8080, allots 512 MB, and expects `SPRING_DATASOURCE_URL` to point at
  the target database.

---

## 10. Known Gaps / Future Work

- **No authentication or authorization** — every endpoint is open. Add Spring Security (JWT/OAuth2)
  before any real-world exposure.
- **No bean-level input validation** — DTOs are not annotated with `@Valid`/`jakarta.validation`
  constraints; invalid payloads reach the service layer.
- **Naming inconsistency** in `CustomerDao` (`existsPersonWithEmail` vs. `existsPersonById`).
- **Plaintext DB credentials** in `application.yaml` and Compose — fine for local dev, should be
  externalized/secret-managed elsewhere.
- The default data access strategy is hard-wired via `@Qualifier`; it could be made configuration-driven.
