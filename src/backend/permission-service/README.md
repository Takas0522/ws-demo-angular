# Permission Service

Permission management service that handles application permissions and user access levels.

## Overview

The Permission Service manages:
- Applications in the system
- Permission levels (READ, WRITE, ADMIN)
- User-Application permission mappings

## Database

- **Database Name**: `permission_db`
- **Port**: 5432 (PostgreSQL)

### Tables

1. **applications**: Stores application information
   - 10 sample applications included in seed data

2. **permission_levels**: Stores permission level definitions
   - READ: Read-only access
   - WRITE: Read and write access
   - ADMIN: Full administrative access

3. **user_app_permissions**: Maps users to applications with specific permission levels
   - Sample permissions for testing included in seed data

## Configuration

### Environment Variables

- `POSTGRES_HOSTNAME`: PostgreSQL host (default: localhost)
- `POSTGRES_USER`: PostgreSQL username (default: postgres)
- `POSTGRES_PASSWORD`: PostgreSQL password (default: postgres)

### Application Properties

- Server Port: 8083
- Database: permission_db
- JPA: Hibernate with PostgreSQL dialect
- SQL Init: Automatic schema and data initialization

## Building and Running

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

Or using java:

```bash
java -jar target/permission-service-1.0.0.jar
```

## Sample Data

The service includes seed data for:
- 10 sample applications (User Management, Inventory, Sales Dashboard, etc.)
- 3 permission levels (READ, WRITE, ADMIN)
- Sample user-app-permission mappings for users 1-7

## Dependencies

- Spring Boot 2.7.18
- Spring Data JPA
- PostgreSQL Driver
- Shared Library (JWT utilities and common DTOs)
- Lombok
