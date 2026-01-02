# DevContainer Init Scripts

This directory contains SQL initialization scripts for PostgreSQL databases in the DevContainer environment.

## Scripts Execution Order

PostgreSQL executes scripts in `/docker-entrypoint-initdb.d` in alphabetical order during container initialization:

1. **01-create-databases.sql** - Creates the three databases (auth_db, user_db, permission_db)
2. **02-init-auth-db.sql** - Initializes auth_db with schema and seed data from auth-service/src/main/resources
3. **03-init-user-db.sql** - Initializes user_db with schema and seed data from user-service/src/main/resources

## Database Initialization

### Auth DB (auth_db)
- **Schema**: users, refresh_tokens, saga_state tables
- **Seed Data**: 10 test users with BCrypt-hashed passwords
  - admin, user1-user6, disabled_user, locked_user, expired_creds_user
  - Default password: `password123`

### User DB (user_db)
- **Schema**: user_profiles, idempotency_keys tables
- **Seed Data**: 10 user profiles corresponding to auth_db users (user_id 1-10)
  - Profile information, contact details, addresses

### Permission DB (permission_db)
- **Status**: Not yet initialized (placeholder for future implementation)

## Notes

- These scripts mirror the `schema.sql` and `data.sql` files in each service's `src/main/resources` directory
- Scripts run only on first container creation (when database volume is empty)
- To re-run initialization, delete the Docker volume: `docker volume rm ws-demo-angular_postgres-data`
- Each service's Spring Boot configuration also includes schema/data initialization, but this DevContainer setup ensures databases are ready before services start

## Maintenance

When updating service schemas or seed data:
1. Update the corresponding files in `src/backend/{service}/src/main/resources/`
2. Update the corresponding init script here in `.devcontainer/init-scripts/`
3. Rebuild the DevContainer or delete and recreate the database volume
