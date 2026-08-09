# Product Catalog

Simple Spring Boot-based Product Catalog service.

## Overview
This project implements a product catalog API using Spring Boot. It provides endpoints to create, read, update and delete product records and persists data via configurable datasources.

## Features
- RESTful API for products (CRUD)
- Validation and error handling
- Unit / integration tests
- Configurable datasource (H2 / PostgreSQL / MySQL)

## Prerequisites
- Java 11+
- Gradle (use the included Gradle wrapper)

## Build (Gradle)

Using the Gradle wrapper (recommended):

Windows:
```powershell
.\gradlew.bat clean build
```

Unix/macOS:
```bash
./gradlew clean build
```

If you have Gradle installed:
```bash
gradle clean build
```

Built artifacts will be in `build/libs/`.

## Run

Run with the Gradle bootRun task:

Windows:
```powershell
.\gradlew.bat bootRun
```

Unix/macOS:
```bash
./gradlew bootRun
```

Run the built JAR:

Windows:
```powershell
java -jar build\libs\<project-name>-<version>.jar
```

Unix/macOS:
```bash
java -jar build/libs/<project-name>-<version>.jar
```

## Tests
Run tests with Gradle:

Windows:
```powershell
.\gradlew.bat test
```

Unix/macOS:
```bash
./gradlew test
```

## Configuration
Application configuration lives in `src/main/resources/application.properties` or `application.yml`. Common settings:
- server.port
- spring.datasource.url / username / password
- spring.jpa.hibernate.ddl-auto

Example H2 for local development:
```properties
spring.datasource.url=jdbc:h2:mem:productdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
```

## API (example)
- GET /api/products — list products
- GET /api/products/{id} — get product by id
- POST /api/products — create product
- PUT /api/products/{id} — update product
- DELETE /api/products/{id} — delete product
