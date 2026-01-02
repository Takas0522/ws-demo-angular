# DevContainer Init Scripts

This directory contains SQL initialization scripts for PostgreSQL databases in the DevContainer environment.

## DRY Approach

**Important**: Database initialization uses a DRY (Don't Repeat Yourself) approach via the postCreateCommand script. The actual schema and data files are referenced from each service's `src/main/resources` directory, ensuring:
- Single source of truth for schema and data
- No sync issues between init scripts and service resources
- Easier maintenance when schemas change

## Scripts Execution Order

### During Container First Start (via `/docker-entrypoint-initdb.d`)
1. **01-create-databases.sql** - Creates the three databases (auth_db, user_db, permission_db)

### During DevContainer Post-Create (via `post-create.sh`)
2. **Auth DB Initialization** - Executes schema/data from `auth-service/src/main/resources/`
3. **User DB Initialization** - Executes schema/data from `user-service/src/main/resources/`

## How It Works

The `post-create.sh` script runs after the DevContainer is created and:
1. Waits for PostgreSQL to be ready
2. Creates databases if they don't exist
3. Checks if tables exist in each database
4. If tables don't exist, executes the schema.sql and data.sql from service resources using `\i` command
5. Skips initialization if tables already exist (idempotent)

Example from post-create.sh:
```bash
PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d auth_db << EOF
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_tables WHERE tablename = 'users') THEN
    \i src/backend/auth-service/src/main/resources/schema.sql
    \i src/backend/auth-service/src/main/resources/data.sql
  END IF;
END
\$\$;
EOF
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

- Database creation runs only on first container start (when volume is empty)
- Schema/data initialization runs during postCreateCommand, checking for existing tables first
- Initialization is idempotent - safe to rebuild container without data loss
- To force re-initialization, delete the Docker volume: `docker volume rm ws-demo-angular_postgres-data`

## Maintenance

**No duplication!** When updating service schemas or seed data:
1. Update only the files in `src/backend/{service}/src/main/resources/`
2. The post-create script will automatically use the updated files
3. Rebuild the DevContainer or delete and recreate the database volume to apply changes
