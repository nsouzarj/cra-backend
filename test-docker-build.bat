@echo off
REM Script to test Docker build process on Windows

echo Testing Docker build process for CRA Backend...

REM Check if Docker is installed
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker is not installed or not in PATH
    echo Please install Docker Desktop to build and run the container
    pause
    exit /b 1
)

REM Check Docker version
echo Docker version:
docker --version

REM Build the Docker image
echo Building Docker image...
docker build -t cra-backend .

if %errorlevel% equ 0 (
    echo Docker build successful!
    echo You can now run the container with:
    echo   docker run -p 8081:8081 cra-backend
) else (
    echo Docker build failed!
    pause
    exit /b 1
)

pause