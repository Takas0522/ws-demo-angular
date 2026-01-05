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

# Create symlink for keys directory (required for services to access keys at /keys/)
echo "Creating symlink for keys directory..."
sudo ln -sf /workspaces/$(basename "$PWD")/keys /keys
echo "Symlink created: /keys -> /workspaces/$(basename "$PWD")/keys"

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
# Check if tables already exist
TABLE_EXISTS=$(PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d auth_db -tAc "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'users');")
if [ "$TABLE_EXISTS" = "f" ]; then
  echo "Initializing auth_db schema and data..."
  PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d auth_db -f src/backend/auth-service/src/main/resources/schema.sql
  PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d auth_db -f src/backend/auth-service/src/main/resources/data.sql
  echo "auth_db initialized successfully"
else
  echo "auth_db tables already exist, skipping initialization"
fi

# Initialize user_db schema and data
echo "Initializing user_db..."
# Check if tables already exist
TABLE_EXISTS=$(PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d user_db -tAc "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'user_profiles');")
if [ "$TABLE_EXISTS" = "f" ]; then
  echo "Initializing user_db schema and data..."
  PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d user_db -f src/backend/user-service/src/main/resources/schema.sql
  PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d user_db -f src/backend/user-service/src/main/resources/data.sql
  echo "user_db initialized successfully"
else
  echo "user_db tables already exist, skipping initialization"
fi

# Initialize permission_db schema and data
echo "Initializing permission_db..."
# Check if tables already exist
TABLE_EXISTS=$(PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d permission_db -tAc "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'applications');")
if [ "$TABLE_EXISTS" = "f" ]; then
  echo "Initializing permission_db schema and data..."
  PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d permission_db -f src/backend/permission-service/src/main/resources/schema.sql
  PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -d permission_db -f src/backend/permission-service/src/main/resources/data.sql
  echo "permission_db initialized successfully"
else
  echo "permission_db tables already exist, skipping initialization"
fi

echo "Database initialization completed!"
echo "Post-create setup completed!"