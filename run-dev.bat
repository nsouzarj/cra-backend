@echo off
REM Batch script to run CRA Backend with local file storage only
echo Starting CRA Backend application with dev profile...

REM Run the application
mvn spring-boot:run -Dspring-boot.run
