@echo off
:: Test script for storage location functionality

echo === Testing Storage Location Functionality ===

:: Configuration
set BASE_URL=http://localhost:8081/cra-api
set TEST_FILE=test-file.txt
set SOLICITACAO_ID=1

:: Create a test file
echo Creating test file...
echo This is a test file for storage location testing. > %TEST_FILE%

echo.
echo === Test 1: Local Storage (should always work) ===
curl -X POST "%BASE_URL%/api/soli-arquivos/upload" ^
  -F "file=@%TEST_FILE%" ^
  -F "solicitacaoId=%SOLICITACAO_ID%" ^
  -F "origem=usuario" ^
  -F "storageLocation=local"

echo.
echo.
echo === Test 2: Google Drive Storage (requires authentication) ===
curl -X POST "%BASE_URL%/api/soli-arquivos/upload" ^
  -F "file=@%TEST_FILE%" ^
  -F "solicitacaoId=%SOLICITACAO_ID%" ^
  -F "origem=usuario" ^
  -F "storageLocation=google_drive"

echo.
echo.
echo === Cleanup: Deleting test file ===
del %TEST_FILE%

echo.
echo Test completed.