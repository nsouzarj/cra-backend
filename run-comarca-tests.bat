@echo off
echo Running Comarca integration tests...
mvn -Dtest=ComarcaIntegrationTest test
echo.
echo Comarca tests completed.
pause