#!/bin/bash

# Stop all Spring Boot services
echo "Stopping all services..."

# Kill all spring-boot:run processes
pkill -f 'spring-boot:run'

# Wait for processes to terminate
sleep 3

# Force kill any remaining processes if necessary
if pgrep -f 'spring-boot:run' > /dev/null; then
    echo "Force stopping remaining processes..."
    pkill -9 -f 'spring-boot:run'
    sleep 2
fi

# Check if processes are stopped
if pgrep -f 'spring-boot:run' > /dev/null; then
    echo "Warning: Some processes may still be running"
    echo "Running processes:"
    ps aux | grep 'spring-boot:run' | grep -v grep
else
    echo "All services stopped successfully!"
fi

# Show port status
echo "Checking ports..."
netstat -tln | grep -E ':(8080|8081|8082|8083).*LISTEN' || echo "All service ports are now free"
