@echo off
echo Running all service tests...
mvn surefire:test
echo.
echo All service tests completed.
pause