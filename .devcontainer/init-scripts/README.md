# DevContainer Init Scripts

This directory contains SQL initialization scripts for PostgreSQL databases in the DevContainer environment.

## DRY Approach

**Important**: These scripts use a DRY (Don't Repeat Yourself) approach by referencing the original SQL files from each service's `src/main/resources` directory instead of duplicating code. This ensures:
- Single source of truth for schema and data
- No sync issues between init scripts and service resources
- Easier maintenance when schemas change

## Scripts Execution Order

PostgreSQL executes scripts in `/docker-entrypoint-initdb.d` in alphabetical order during container initialization:

1. **01-create-databases.sql** - Creates the three databases (auth_db, user_db, permission_db)
2. **02-init-auth-db.sql** - Connects to auth_db and executes schema/data from `auth-service/src/main/resources/`
3. **03-init-user-db.sql** - Connects to user_db and executes schema/data from `user-service/src/main/resources/`

## How It Works

The PostgreSQL container has the backend source directory mounted at `/backend-src`:
```yaml
volumes:
  - ../../src/backend:/backend-src:ro
```

Init scripts use PostgreSQL's `\i` command to include SQL files from the mounted resources:
```sql
\c user_db;
\i /backend-src/user-service/src/main/resources/schema.sql
\i /backend-src/user-service/src/main/resources/data.sql
```

## Database Initialization

### Auth DB (auth_db)
- **Source**: `auth-service/src/main/resources/schema.sql` and `data.sql`
- **Schema**: users, refresh_tokens, saga_state tables
- **Seed Data**: 10 test users with BCrypt-hashed passwords
  - admin, user1-user6, disabled_user, locked_user, expired_creds_user
  - Default password: `password123`

### User DB (user_db)
- **Source**: `user-service/src/main/resources/schema.sql` and `data.sql`
- **Schema**: user_profiles, idempotency_keys tables
- **Seed Data**: 10 user profiles corresponding to auth_db users (user_id 1-10)
  - Profile information, contact details, addresses

### Permission DB (permission_db)
- **Status**: Not yet initialized (placeholder for future implementation)

## Notes

- Scripts run only on first container creation (when database volume is empty)
- To re-run initialization, delete the Docker volume: `docker volume rm ws-demo-angular_postgres-data`
- Each service's Spring Boot configuration also includes schema/data initialization, but this DevContainer setup ensures databases are ready before services start

## Maintenance

**No duplication!** When updating service schemas or seed data:
1. Update only the files in `src/backend/{service}/src/main/resources/`
2. The init scripts will automatically use the updated files
3. Rebuild the DevContainer or delete and recreate the database volume to apply changes
