@echo off
echo Running controller integration tests...
mvn -Dtest=*ControllerIntegrationTest test
echo.
echo Controller integration tests completed.
pause