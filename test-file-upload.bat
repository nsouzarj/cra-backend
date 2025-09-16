@echo off
REM Test file upload endpoint

REM Create a test file
echo This is a test file for upload testing > test-upload.txt

REM Upload the file using curl
curl -X POST ^
  http://192.168.1.103:8081/cra-api/api/soli-arquivos/upload ^
  -H "content-type: multipart/form-data" ^
  -F "file=@test-upload.txt" ^
  -F "solicitacaoId=1" ^
  -F "origem=usuario"

REM Clean up
del test-upload.txt