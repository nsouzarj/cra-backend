@echo off
echo Configuring Google Drive Integration for CRA Backend
echo ======================================================

REM Check if environment variables are already set
if defined GOOGLE_DRIVE_CLIENT_ID (
    echo GOOGLE_DRIVE_CLIENT_ID is already set
) else (
    echo Please set the following environment variables:
    echo set GOOGLE_DRIVE_CLIENT_ID=your-client-id
    echo set GOOGLE_DRIVE_CLIENT_SECRET=your-client-secret
    echo.
    echo You can get these values from the Google Cloud Console.
    echo.
)

if defined GOOGLE_DRIVE_CLIENT_SECRET (
    echo GOOGLE_DRIVE_CLIENT_SECRET is already set
) else (
    echo Please set the GOOGLE_DRIVE_CLIENT_SECRET environment variable
)

echo.
echo To enable Google Drive integration:
echo 1. Set the environment variables above
echo 2. Make sure google.drive.oauth.enabled=true in application-dev.properties
echo 3. Run the application with: mvn spring-boot:run -Dspring-boot.run.profiles=dev
echo.

pause