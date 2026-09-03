# Inventory API

REST API for managing products and processing purchases while validating product stock.

## Technologies

- Java 17
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Jakarta Validation
- H2 Database
- Maven
- JUnit 5
- Mockito
- MockMvc

## Project Structure

The project is organized by feature and uses conventional Spring layers:

```text
product
├── controller
├── dto
├── entity
├── exception
├── repository
└── service

common
└── exception
```

### Responsibilities

- `controller`: exposes REST endpoints.
- `dto`: defines request and response payloads.
- `entity`: contains the JPA entity and business rules.
- `service`: coordinates use cases and transactions.
- `repository`: provides database access.
- `exception`: contains business exceptions.
- `common.exception`: provides centralized API error handling.

## Running the Application

Run the tests:

```bash
./mvnw clean test
```

Start the application:

```bash
./mvnw spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

The application uses an in-memory H2 database. Data is cleared when the application is restarted.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/products` | List all products |
| `GET` | `/products/{id}` | Find a product by ID |
| `POST` | `/products` | Create a product |
| `PUT` | `/products/{id}` | Update a product |
| `DELETE` | `/products/{id}` | Delete a product |
| `POST` | `/products/{id}/purchase` | Purchase product units |
| GET | `/products/low-stock?threshold=5` | List products with stock less than or equal to the threshold, ordered ascending (default: 5) |

## Create a Product

```bash
curl -i -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":3500.00,"stock":10}'
```

Example response:

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 3500.00,
  "stock": 10
}
```

## Purchase a Product

```bash
curl -i -X POST http://localhost:8080/products/1/purchase \
  -H "Content-Type: application/json" \
  -d '{"quantity":2}'
```

Example response:

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 3500.00,
  "stock": 8
}
```

## Business Rules

- Product names must not be blank.
- Prices must be greater than zero.
- Initial stock cannot be negative.
- Purchase quantity must be greater than zero.
- A purchase cannot leave the product with negative stock.
- Insufficient stock returns `409 Conflict`.
- Missing products return `404 Not Found`.
- Invalid requests return `400 Bad Request`.
- Concurrent product updates are protected with optimistic locking.

## Error Response

Example:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Insufficient stock available: 8 requested: 20",
  "timestamp": "2026-09-03T12:09:58Z"
}
```

## Testing

The project includes:

- Service unit tests using JUnit 5 and Mockito.
- Controller validation and error-mapping tests using MockMvc.
- A Spring application context test.
- JPA integration test verifies optimistic locking behavior using H2.

Run all tests with:

```bash
./mvnw test
```

## Design Decisions

- `BigDecimal` is used for monetary values.
- DTOs prevent exposing persistence entities through the API.
- Business rules are located inside the `Product` entity.
- Service methods use transactions for state-changing operations.
- API exceptions are handled centrally with `GlobalExceptionHandler`.
- JPA `@Version` is used to detect concurrent stock updates and prevent lost updates.