@echo off
echo Testing local file storage functionality...

REM Create a test file
echo This is a test file for local storage testing. > test-file.txt
echo Created test file: test-file.txt

REM Test file upload
echo.
echo Testing file upload...
curl -X POST "http://localhost:8081/cra-api/api/soli-arquivos/upload" ^
  -F "file=@test-file.txt" ^
  -F "solicitacaoId=1" ^
  -F "origem=test"

echo.
echo File upload test completed.

REM Clean up test files
del test-file.txt >nul 2>&1

echo.
echo Test completed.
pause