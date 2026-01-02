#!/bin/bash
set -e

echo "Running post-create setup..."

# Generate JWT RSA keys if they don't exist
if [ ! -f .devcontainer/keys/private_key.pem ] || [ ! -f .devcontainer/keys/public_key.pem ]; then
  echo "Generating JWT RSA key pair..."
  bash .devcontainer/generate-keys.sh
else
  echo "JWT RSA keys already exist, skipping generation"
fi

# Install npm dependencies if package.json exists
if [ -f package.json ]; then
  echo "Installing npm dependencies..."
  npm install
else
  echo "No package.json found, skipping npm install"
fi

# Initialize PostgreSQL databases
echo "Initializing PostgreSQL databases..."

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
until PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -c '\q' 2>/dev/null; do
  echo "PostgreSQL is unavailable - sleeping"
  sleep 2
done
echo "PostgreSQL is ready!"

# Create databases if they don't exist
echo "Creating databases..."
PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -f .devcontainer/init-scripts/01-create-databases.sql 2>/dev/null || echo "Databases already exist"

# Initialize auth_db schema and data
echo "Initializing auth_db..."
PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d auth_db << EOF
-- Check if tables already exist
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'users') THEN
    -- Execute schema
    \i src/backend/auth-service/src/main/resources/schema.sql
    -- Execute data
    \i src/backend/auth-service/src/main/resources/data.sql
    RAISE NOTICE 'auth_db initialized successfully';
  ELSE
    RAISE NOTICE 'auth_db tables already exist, skipping initialization';
  END IF;
END
\$\$;
EOF

# Initialize user_db schema and data
echo "Initializing user_db..."
PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d user_db << EOF
-- Check if tables already exist
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'user_profiles') THEN
    -- Execute schema
    \i src/backend/user-service/src/main/resources/schema.sql
    -- Execute data
    \i src/backend/user-service/src/main/resources/data.sql
    RAISE NOTICE 'user_db initialized successfully';
  ELSE
    RAISE NOTICE 'user_db tables already exist, skipping initialization';
  END IF;
END
\$\$;
EOF

echo "Database initialization completed!"
echo "Post-create setup completed!"
