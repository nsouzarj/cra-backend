@echo off
echo Running ComarcaService tests...
mvn -Dtest=ComarcaServiceTest test
echo.
echo ComarcaService tests completed.
pause