@echo off
echo Testing Swagger Documentation
echo ===========================

echo.
echo To view the Swagger documentation:
echo 1. Start the application with: mvn spring-boot:run -Dspring-boot.run.profiles=dev
echo 2. Open your browser and navigate to: http://localhost:8081/cra-api/swagger-ui.html
echo.
echo You should see the following API tags:
echo - auth
echo - comarca
echo - correspondente
echo - orgao
echo - processo
echo - solicitacao
echo - status-solicitacao
echo - tipo-solicitacao
echo - uf
echo - usuario
echo - soli-arquivo
echo - google-drive
echo.
echo The new Google Drive and Soli Arquivo endpoints should be properly documented.
echo.

pause