# Running CRA Backend with Local File Storage Only

This document explains how to run the CRA Backend application with local file storage only (Google Drive OAuth integration has been removed).

## Prerequisites

Make sure you have the following installed:
- Java 17+
- Maven 3.6+

## Running with Batch Script (Windows Command Prompt)

Use the [run-dev.bat](file:///d%3A/Projetos/craweb/cra-backend/run-dev.bat) script to run the application:

```cmd
run-dev.bat
```

## Running with PowerShell Script

Use the [run-dev.ps1](file:///d%3A/Projetos/craweb/cra-backend/run-dev.ps1) script to run the application:

```powershell
.\run-dev.ps1
```

## Running Manually

If you prefer to run the application manually:

### Windows Command Prompt:
```cmd
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### PowerShell:
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Configuration Files

The application uses the following configuration files:
- [application.properties](file:///d%3A/Projetos/craweb/cra-backend/src/main/resources/application.properties) - Base configuration
- [application-dev.properties](file:///d%3A/Projetos/craweb/cra-backend/src/main/resources/application-dev.properties) - Development profile configuration

## File Storage

All uploaded files are now stored locally in the filesystem:
- Development: `./uploads` directory
- Production: `/app/uploads` directory
- Test: `/tmp/cra-test-uploads` directory

Files are given unique names using UUID + original filename pattern to prevent conflicts.