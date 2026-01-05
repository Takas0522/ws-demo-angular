#!/bin/bash

# Stop all services
echo "Stopping all services..."
pkill -f 'spring-boot:run'
sleep 3

# Start Auth Service
echo "Starting Auth Service..."
cd /workspaces/ws-demo-angular/src/backend/auth-service
mvn spring-boot:run > /tmp/auth-service.log 2>&1 &
sleep 10

# Start User Service
echo "Starting User Service..."
cd /workspaces/ws-demo-angular/src/backend/user-service
mvn spring-boot:run > /tmp/user-service.log 2>&1 &
sleep 10

# Start Permission Service
echo "Starting Permission Service..."
cd /workspaces/ws-demo-angular/src/backend/permission-service
mvn spring-boot:run > /tmp/permission-service.log 2>&1 &
sleep 10

# Start BFF Service
echo "Starting BFF Service..."
cd /workspaces/ws-demo-angular/src/backend/bff-service
mvn spring-boot:run > /tmp/bff-service.log 2>&1 &

echo "Waiting for services to start..."
sleep 30

# Check services
echo "Checking services..."
netstat -tln | grep -E ':(8080|8081|8082|8083).*LISTEN'

echo "All services started!"
