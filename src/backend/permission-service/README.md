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

## API Endpoints

### Application Management

#### GET /api/applications
Get all applications in the system.

**Response:**
```json
[
  {
    "id": 1,
    "appCode": "USER_MGMT",
    "appName": "User Management",
    "description": "Manage user accounts, profiles, and authentication",
    "enabled": true
  }
]
```

### Permission Management

#### GET /api/users/{userId}/permissions
Get all permissions for a specific user.

**Response:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "application": {
      "id": 1,
      "appCode": "USER_MGMT",
      "appName": "User Management",
      "enabled": true
    },
    "permissionLevel": {
      "id": 3,
      "levelCode": "ADMIN",
      "levelName": "Administrator",
      "levelOrder": 3
    },
    "grantedAt": "2024-01-01T12:00:00",
    "grantedBy": "system",
    "expiresAt": null
  }
]
```

#### POST /api/permissions
Create a new permission for a user.

**Request Body:**
```json
{
  "userId": 1,
  "applicationId": 1,
  "permissionLevelId": 2,
  "grantedBy": "admin",
  "expiresAt": null
}
```

**Response:**
```json
{
  "id": 15,
  "userId": 1,
  "application": {...},
  "permissionLevel": {...},
  "grantedAt": "2024-01-01T12:00:00",
  "grantedBy": "admin",
  "expiresAt": null
}
```

#### PUT /api/permissions/{permissionId}
Update an existing permission.

**Request Body:**
```json
{
  "permissionLevelId": 3,
  "expiresAt": "2025-12-31T23:59:59",
  "grantedBy": "admin"
}
```

**Response:**
```json
{
  "id": 15,
  "userId": 1,
  "application": {...},
  "permissionLevel": {...},
  "grantedAt": "2024-01-01T12:00:00",
  "grantedBy": "admin",
  "expiresAt": "2025-12-31T23:59:59"
}
```

#### DELETE /api/permissions/{permissionId}
Delete a permission.

**Response:** 204 No Content

## Authentication

All API endpoints require JWT authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <jwt-token>
```

The JWT token must be signed with the private key corresponding to the public key configured at `/keys/public_key.pem`.

## Audit Logging

All permission changes (CREATE, UPDATE, DELETE) are automatically logged in the `permission_audit_log` table with:
- User ID
- Application ID
- Permission Level ID
- Action performed
- Who performed the action
- Timestamp
- Old and new permission levels (for updates)

## Sample Data

The service includes seed data for:
- 10 sample applications (User Management, Inventory, Sales Dashboard, etc.)
- 3 permission levels (READ, WRITE, ADMIN)
- Sample user-app-permission mappings for users 1-7

## Dependencies

- Spring Boot 2.7.18
- Spring Data JPA
- Spring Security
- PostgreSQL Driver
- Shared Library (JWT utilities and common DTOs)
- Lombok
