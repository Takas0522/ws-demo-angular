#!/bin/bash
set -e

echo "Generating JWT RSA key pair..."

# Create keys directory if it doesn't exist
mkdir -p .devcontainer/keys

# Generate 2048-bit RSA private key
echo "Generating private key..."
openssl genrsa -out .devcontainer/keys/private_key.pem 2048

# Extract public key from private key
echo "Extracting public key..."
openssl rsa -in .devcontainer/keys/private_key.pem -pubout -out .devcontainer/keys/public_key.pem

# Set restrictive permissions on private key
echo "Setting file permissions..."
chmod 600 .devcontainer/keys/private_key.pem
chmod 644 .devcontainer/keys/public_key.pem

echo "RSA key pair generated successfully!"
echo "Private key: .devcontainer/keys/private_key.pem"
echo "Public key: .devcontainer/keys/public_key.pem"
