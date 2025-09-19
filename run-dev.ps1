# PowerShell script to run CRA Backend with local file storage only
Write-Host "Starting CRA Backend application with dev profile..."

# Run the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
