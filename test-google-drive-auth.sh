#!/bin/bash

# Google Drive Authentication and File Upload Test Script
# This script demonstrates the complete Google Drive OAuth2 flow

echo "=== Google Drive Authentication and File Upload Test ==="

# Configuration
BASE_URL="http://localhost:8081/cra-api"
USER_ID=1
TEST_FILE="./test-file.txt"

# Create a test file
echo "Creating test file..."
echo "This is a test file for Google Drive integration testing." > $TEST_FILE

# Step 1: Get authorization URL
echo "Step 1: Getting authorization URL..."
AUTH_RESPONSE=$(curl -s -X GET "$BASE_URL/api/google-drive/authorize?userId=$USER_ID")
echo "Authorization response: $AUTH_RESPONSE"

# Extract authorization URL (in a real script, you'd parse JSON properly)
AUTH_URL=$(echo $AUTH_RESPONSE | grep -o '"authorizationUrl":"[^"]*"' | cut -d'"' -f4)
echo "Authorization URL: $AUTH_URL"

# Note: In a real scenario, you would open this URL in a browser and complete authentication
echo "Please open the following URL in your browser and complete Google authentication:"
echo $AUTH_URL
echo ""

# Step 2: After authentication, check connection status
echo "Step 2: Checking connection status (after authentication)..."
STATUS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/google-drive/status?userId=$USER_ID")
echo "Status response: $STATUS_RESPONSE"

# Step 3: Upload a test file (requires valid JWT token)
echo "Step 3: Uploading test file to Google Drive..."
echo "Note: This requires a valid JWT token. Please authenticate first and get a token."

# Example upload command (commented out since it requires a valid token):
# curl -X POST "$BASE_URL/api/soli-arquivos/upload" \
#   -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
#   -F "file=@$TEST_FILE" \
#   -F "solicitacaoId=1" \
#   -F "origem=usuario" \
#   -F "storageLocation=google_drive"

echo "Upload command example (replace YOUR_JWT_TOKEN_HERE with a valid token):"
echo "curl -X POST \"$BASE_URL/api/soli-arquivos/upload\" \\"
echo "  -H \"Authorization: Bearer YOUR_JWT_TOKEN_HERE\" \\"
echo "  -F \"file=@$TEST_FILE\" \\"
echo "  -F \"solicitacaoId=1\" \\"
echo "  -F \"origem=usuario\" \\"
echo "  -F \"storageLocation=google_drive\""

# Cleanup
echo "Cleaning up test file..."
rm $TEST_FILE

echo ""
echo "=== Test Complete ==="
echo "Please follow the instructions above to complete the Google Drive authentication flow."