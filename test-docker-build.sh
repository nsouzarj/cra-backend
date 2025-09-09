#!/bin/bash

# Script to test Docker build process
echo "Testing Docker build process for CRA Backend..."

# Check if Docker is installed
if ! command -v docker &> /dev/null
then
    echo "Docker is not installed or not in PATH"
    echo "Please install Docker to build and run the container"
    exit 1
fi

# Check Docker version
echo "Docker version:"
docker --version

# Build the Docker image
echo "Building Docker image..."
docker build -t cra-backend .

if [ $? -eq 0 ]; then
    echo "Docker build successful!"
    echo "You can now run the container with:"
    echo "  docker run -p 8081:8081 cra-backend"
else
    echo "Docker build failed!"
    exit 1
fi