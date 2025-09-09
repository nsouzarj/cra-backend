#!/bin/bash

echo "=== Testing CRA Backend API ==="
echo ""

# Test 1: Health Check (Database Info)
echo "1. Testing Database Info endpoint..."
curl -X GET "http://localhost:8080/cra-api/api/auth/database-info" \
  -H "Accept: application/json" \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo ""
echo "----------------------------------------"
echo ""

# Test 2: Login with admin credentials
echo "2. Testing Login endpoint with admin credentials..."
curl -X POST "http://localhost:8080/cra-api/api/auth/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "login": "admin",
    "senha": "admin123"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo ""
echo "----------------------------------------"
echo ""

# Test 3: Login with isomina credentials  
echo "3. Testing Login endpoint with isomina credentials..."
curl -X POST "http://localhost:8080/cra-api/api/auth/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "login": "isomina",
    "senha": "isomina123"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo ""
echo "=== Test completed ==="