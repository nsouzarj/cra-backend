#!/bin/bash

# Script to test Swagger API documentation
echo "Testing Swagger API Documentation"
echo "================================="

# Check if the application is running
echo "Checking if application is running..."
curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/cra-api/api-docs > /tmp/curl_result.txt
HTTP_CODE=$(cat /tmp/curl_result.txt)

if [ "$HTTP_CODE" = "200" ]; then
    echo "✓ API Documentation is accessible"
    
    # Get the API documentation
    echo "Downloading API documentation..."
    curl -s http://localhost:8081/cra-api/api-docs > /tmp/api-docs.json
    
    # Check if our new endpoints are in the documentation
    echo "Checking for Google Drive endpoints..."
    if grep -q "google-drive" /tmp/api-docs.json; then
        echo "✓ Google Drive endpoints found in documentation"
    else
        echo "✗ Google Drive endpoints NOT found in documentation"
    fi
    
    echo "Checking for Soli Arquivo endpoints..."
    if grep -q "soli-arquivos" /tmp/api-docs.json; then
        echo "✓ Soli Arquivo endpoints found in documentation"
    else
        echo "✗ Soli Arquivo endpoints NOT found in documentation"
    fi
    
    # Clean up
    rm /tmp/api-docs.json /tmp/curl_result.txt
    
    echo ""
    echo "To view the interactive documentation:"
    echo "Open your browser and go to: http://localhost:8081/cra-api/swagger-ui.html"
else
    echo "✗ API Documentation is NOT accessible (HTTP $HTTP_CODE)"
    echo "Please make sure the application is running:"
    echo "mvn spring-boot:run -Dspring-boot.run.profiles=dev"
    rm /tmp/curl_result.txt
fi