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
if PGPASSWORD=postgres psql -h wsdemoangulardb -U postgres -f .devcontainer/init-scripts/01-create-databases.sql 2>/dev/null; then
  echo "Databases created successfully"
else
  echo "Databases already exist or initialization skipped"
fi

echo "Post-create setup completed!"
