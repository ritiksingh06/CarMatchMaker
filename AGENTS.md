# AI Agent Instructions

## Project Overview
Full-stack web application built with Java and Spring Boot, containerized with Docker and deployable using docker-compose for production-ready deployment.

## Technology Stack
- **Language**: Java (prefer Java 21 or later)
- **Framework**: Spring Boot 3.x
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **Boilerplate Reduction**: Lombok
- **Containerization**: Docker
- **Orchestration**: docker-compose
- **Architecture**: Full-stack web application (backend + frontend)

## Build & Run Commands

### Local Development
```bash
# Build the application
./mvnw clean install

# Run locally
./mvnw spring-boot:run

# Run tests
./mvnw test
```

### Docker Development
```bash
# Build Docker image
docker build -t demo-project:latest .

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## Project Structure Conventions

### Recommended Directory Layout
```
/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/demo/
│   │   │       ├── controller/     # REST controllers
│   │   │       ├── service/        # Business logic
│   │   │       ├── repository/     # Data access layer
│   │   │       ├── model/          # Domain entities
│   │   │       ├── dto/            # Data transfer objects
│   │   │       ├── config/         # Spring configuration classes
│   │   │       └── DemoApplication.java
│   │   └── resources/
│   │       ├── application.yml     # Main configuration
│   │       ├── application-dev.yml # Development profile
│   │       ├── application-prod.yml # Production profile
│   │       ├── static/             # Frontend assets (CSS, JS, images)
│   │       └── templates/          # HTML templates (if using Thymeleaf)
│   └── test/
│       └── java/
├── docker/                          # Docker-related files
├── docker-compose.yml               # Container orchestration
├── Dockerfile                       # Application container definition
├── pom.xml                          # Maven build configuration
└── README.md                        # Project documentation
```

## Development Guidelines

### Spring Boot Best Practices
1. **Configuration Management**
   - Use `application.yml` for configuration (prefer over .properties)
   - Leverage Spring profiles (dev, test, prod) for environment-specific configs
   - Externalize sensitive data (use environment variables in docker-compose)

2. **Layered Architecture**
   - **Controller Layer**: Handle HTTP requests, validate input, return DTOs
   - **Service Layer**: Contain business logic, transaction management
   - **Repository Layer**: Data access using Spring Data JPA
   - Keep layers decoupled using interfaces

3. **REST API Conventions**
   - Use `@RestController` for REST endpoints
   - Follow RESTful URL patterns: `/api/v1/resources`
   - Use appropriate HTTP methods (GET, POST, PUT, DELETE)
   - Return proper HTTP status codes
   - Use `@Valid` for request validation

4. **Exception Handling**
   - Implement global exception handler with `@ControllerAdvice`
   - Return consistent error response structure
   - Log exceptions appropriately

5. **Database Access**
   - Use Spring Data JPA for data persistence
   - Define entities in `model/` package with proper JPA annotations
   - Use repository interfaces extending `JpaRepository`
   - Consider using Liquibase or Flyway for database migrations

### Docker & Deployment

1. **Dockerfile Requirements**
   - Use multi-stage builds to reduce image size
   - Base image: `eclipse-temurin:17-jre-alpine` or similar
   - Copy JAR file from build stage
   - Expose application port (typically 8080)
   - Use non-root user for security
   - Set appropriate ENTRYPOINT

2. **docker-compose.yml Structure**
   - Define application service
   - Include PostgreSQL database service
   - Set up networks for service communication
   - Use volumes for data persistence
   - Expose only necessary ports (app: 8080, db: 5432)
   - Set environment variables for configuration

3. **Environment Variables**
   - Database connection details (host, port, username, password)
   - Application port
   - Active Spring profile
   - Any external service credentials

### Frontend Integration
- **Static Resources**: Place in `src/main/resources/static/`
- **Template Engine**: Use Thymeleaf for server-side rendering
- **SPA Integration**: Serve React/Angular/Vue builds from static folder
- **API Prefix**: Use `/api` for REST endpoints to avoid conflicts

## Common Tasks

### Adding a New REST Endpoint
1. Create DTO classes in `dto/` package
2. Create or update service in `service/` package
3. Create controller in `controller/` package with proper annotations
4. Write unit tests for service and integration tests for controller

### Adding Database Entity
1. Create entity class in `model/` with JPA annotations
2. Create repository interface extending `JpaRepository`
3. Update database migration scripts if using Flyway/Liquibase
4. Create corresponding DTO for API responses

### Modifying Docker Configuration
1. Update `Dockerfile` for build/runtime changes
2. Update `docker-compose.yml` for service orchestration
3. Test locally with `docker-compose up --build`
4. Verify all services start and communicate correctly

## Security Considerations
- Enable Spring Security for authentication/authorization
- Use HTTPS in production (configure reverse proxy or Spring SSL)
- Validate all user inputs
- Implement CORS configuration for frontend integration
- Use environment variables for secrets (never hardcode)

## Testing Strategy
- **Unit Tests**: Test services and utility classes (JUnit 5 + Mockito)
- **Integration Tests**: Test REST endpoints with `@SpringBootTest` and MockMvc
- **Test Containers**: Use Testcontainers for database integration tests
- Aim for high coverage of business logic

## Performance & Monitoring
- Enable Spring Boot Actuator for health checks and metrics
- Configure logging levels appropriately per environment
- Use connection pooling for database (HikariCP - default in Spring Boot)
- Consider adding Prometheus metrics endpoint for monitoring

## When Generating Code
- Use Java 17+ features (records, sealed classes, pattern matching)
- Follow Spring Boot conventions and idioms
- Use Lombok annotations (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor) for entities and POJOs
- Use Java records for DTOs when immutability is desired
- Generate both implementation and corresponding tests
- Include proper exception handling
- Add appropriate logging statements
- Use constructor injection for dependencies
- Apply SOLID principles
