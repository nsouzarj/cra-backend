@echo off
echo Running complete test suite with coverage...
echo.
echo 1. Running unit tests...
mvn surefire:test
echo.
echo 2. Running integration tests...
mvn failsafe:integration-test
echo.
echo 3. Generating coverage reports...
mvn jacoco:report jacoco:report-integration
echo.
echo Coverage reports generated in:
echo - Unit test coverage: target/site/jacoco/index.html
echo - Integration test coverage: target/site/jacoco-it/index.html
echo.
echo Opening unit test coverage report...
start target/site/jacoco/index.html
pause