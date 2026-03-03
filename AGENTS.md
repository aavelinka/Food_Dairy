# AGENTS.md - Food Diary API Development Guide
## This file provides guidelines for agentic coding agents working on this Java Spring Boot project.
## Build Commands
### Basic Operations
```
./mvnw clean compile          # Compile the project
./mvnw clean package          # Build JAR file
./mvnw spring-boot:run        # Run the application
```
### Testing
```
./mvnw test                   # Run all tests
./mvnw test -Dtest=ClassName#methodName   # Run single test method
./mvnw test -Dtest=ClassName               # Run all tests in a class
./mvnw verify                 # Run tests and verify (no install)
```
### Linting & Code Quality
```
./mvnw checkstyle:checkstyle  # Run Checkstyle only
./mvnw checkstyle:check        # Checkstyle with violations
```
### Full Build (CI Pipeline)
```
./mvnw -B -DskipTests verify   # Full build without tests (used in CI)
```
---
## Code Style Guidelines
### General Rules
- Max line length: 120 characters
- Indentation: 4 spaces (8 for line continuations)
- No star imports: Use explicit imports only
- Encoding: UTF-8
### Naming Conventions
- Classes/Types: PascalCase (e.g., UserController, BodyParameters)
- Methods: camelCase (e.g., getUserById, userCreate)
- Variables: camelCase
- Constants: UPPER_SNAKE_CASE (e.g., USER_FAIL_MESSAGE)
- Packages: lowercase with dots (e.g., com.uni.project.controller)
### Project Structure
  src/main/java/com/uni/project/
  ├── controller/       # REST endpoints (@RestController)
  ├── service/          # Business logic interfaces
  │   └── impl/        # Service implementations
  ├── repository/      # Spring Data JPA repositories
  ├── model/
  │   ├── entity/     # JPA entities (@Entity)
  │   ├── dto/
  │   │   ├── request/   # Request DTOs
  │   │   └── response/  # Response DTOs
  ├── mapper/         # MapStruct mappers
  └── exception/     # Custom exceptions (RuntimeException)
### Architecture Patterns 
### Controllers
- Use @RestController with @RequestMapping
- Use @AllArgsConstructor for dependency injection
- Use @Valid for request body validation
- Return proper ResponseEntity with appropriate HTTP status
### Services
- Use @Service annotation
- Use @AllArgsConstructor for dependency injection
- Place @Transactional(readOnly = true) at class level
- Override with @Transactional for write operations
### Entities
- Use Lombok: @Entity, @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor
- Use @EqualsAndHashCode(onlyExplicitlyIncluded = true) with @EqualsAndHashCode.Include on ID
### Mappers (MapStruct)
- Use @Mapper(componentModel = "spring", uses = ...)
### Exceptions
- Create custom exceptions extending RuntimeException
### Testing
- Tests use Testcontainers for PostgreSQL
- Use @Import(TestcontainersConfiguration.class) for test configuration
- Use @SpringBootTest for integration tests
---
## Technology Stack
- Java: 21
- Framework: Spring Boot 4.0.2
- Build Tool: Maven (with wrapper mvnw)
- Database: PostgreSQL (via Spring Data JPA / Hibernate)
- ORM: Spring Data JPA, MapStruct 1.6.3
- Lombok: For getters, setters, constructors
- Testing: JUnit 5, Testcontainers
