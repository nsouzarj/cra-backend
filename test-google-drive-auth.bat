@echo off
:: Google Drive Authentication and File Upload Test Script
:: This script demonstrates the complete Google Drive OAuth2 flow

echo === Google Drive Authentication and File Upload Test ===

:: Configuration
set BASE_URL=http://localhost:8081/cra-api
set TEST_FILE=test-file.txt

:: Create a test file
echo Creating test file...
echo This is a test file for Google Drive integration testing. > %TEST_FILE%

:: Step 1: Get authorization URL
echo Step 1: Getting authorization URL...
echo Please open the following URL in your browser and complete the authentication:
curl -s -X GET "%BASE_URL%/api/google-drive/auth-url"
echo.

:: Step 2: After authentication, check token status
echo.
echo Step 2: Checking token status (after authentication)...
curl -s -X GET "%BASE_URL%/api/google-drive/token-status"
echo.

:: Step 3: Check connection status
echo.
echo Step 3: Checking connection status...
curl -s -X GET "%BASE_URL%/api/google-drive/status"
echo.

:: Step 4: Upload a test file (requires valid JWT token)
echo.
echo Step 4: To upload a test file, use the following command:
echo curl -X POST "%BASE_URL%/api/soli-arquivos/upload" ^
echo   -H "Authorization: Bearer YOUR_JWT_TOKEN" ^
echo   -F "file=@%TEST_FILE%" ^
echo   -F "solicitacaoId=1" ^
echo   -F "origem=usuario" ^
echo   -F "storageLocation=google_drive"

echo.
echo Cleanup: Deleting test file...
del %TEST_FILE%

echo.
echo Test completed.