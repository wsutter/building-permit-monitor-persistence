# Persistence Module

## Purpose

The Persistence service stores enriched building permit data in PostgreSQL.

It represents the final stage of the event processing pipeline.

## Responsibilities

### Event Consumption

Consumes:

```text
building-permit.enriched
```

using:

```java
KafkaGroupIDs.PERSISTENCE
```

### Database Persistence

Stores permits in PostgreSQL.

Typical data includes:

* permit identifiers
* publication information
* address information
* geolocation
* metadata

### Spatial Queries

The module supports geographic searches using PostGIS.

Example use cases:

* permits within radius
* nearby permit searches
* map-based exploration

### Flyway Migrations

Database schema changes are managed using Flyway.

Migration scripts are located in:

```text
src/main/resources/db/migration
```

Apply migrations:

```bash
mvn flyway:migrate
```

### Error Handling

Failed messages are routed to:

```text
building-permit.enriched.dlq
```

## Event Flow

```text
building-permit.enriched
            ↓
        Persistence
            ↓
        PostgreSQL
```

## Technologies / Frameworks

* Java 25
* Spring Boot 4
* Spring Data JPA
* PostgreSQL
* PostGIS
* Flyway

## Startup

```bash
mvn spring-boot:run -pl persistence
```
