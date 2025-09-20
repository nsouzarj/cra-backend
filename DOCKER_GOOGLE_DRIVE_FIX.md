# Fixing Google Drive Endpoint Issues in Docker

## Problem
The error `org.springframework.web.servlet.resource.NoResourceFoundException: No static resource api/google-drive/status` indicates that the Google Drive controller endpoints are not being found in the Docker environment.

## Root Causes

1. **Inconsistent Context Path Configuration**: The main [application.properties](file:///d:/Projetos/craweb/cra-backend/src/main/resources/application.properties#L7-L7) has `server.servlet.context-path=/cra-api`, but [application-prod.properties](file:///d:/Projetos/craweb/cra-backend/src/main/resources/application-prod.properties#L1-L84) was missing this setting, causing inconsistent endpoint URLs between development and production environments.

2. **Docker Container Not Rebuilt**: Changes to configuration files may not be reflected in the Docker container if it's not rebuilt.

## Solution

### 1. Ensure Context Path Consistency

Make sure all environment-specific properties files include the context path:

**In [application-prod.properties](file:///d:/Projetos/craweb/cra-backend/src/main/resources/application-prod.properties#L1-L84):**
```properties
# SERVER CONFIGURATION
server.port=8081
server.servlet.context-path=/cra-api
```

### 2. Rebuild Docker Images

After making configuration changes, rebuild the Docker images:

```bash
# Stop running containers
docker-compose down

# Rebuild images with no cache
docker-compose build --no-cache

# Start containers
docker-compose up
```

### 3. Verify Endpoint URLs

With the context path `/cra-api`, the Google Drive endpoints should be accessible at:
- Status: `http://localhost:8081/cra-api/api/google-drive/status`
- Token Status: `http://localhost:8081/cra-api/api/google-drive/token-status`
- Authorization URL: `http://localhost:8081/cra-api/api/google-drive/auth-url`

### 4. Test Endpoints

You can test the endpoints with curl:

```bash
# Test status endpoint
curl http://localhost:8081/cra-api/api/google-drive/status

# Test token status endpoint
curl http://localhost:8081/cra-api/api/google-drive/token-status

# Get authorization URL
curl http://localhost:8081/cra-api/api/google-drive/auth-url
```

## Additional Troubleshooting

### Check Application Logs
If endpoints are still not found, check the Spring Boot application logs for any errors during startup:

```bash
docker-compose logs cra-backend
```

Look for messages like:
- "Mapped "{[/api/google-drive/status]" - This indicates the endpoint is registered
- "Error creating bean" - This indicates configuration issues

### Verify Component Scanning
Ensure the controller is in the correct package and properly annotated:
- Package: `br.adv.cra.controller`
- Annotations: `@RestController`, `@RequestMapping`

### Check for Profile-Specific Issues
Verify that the `prod` profile is correctly activated and that all required properties are set in [application-prod.properties](file:///d:/Projetos/craweb/cra-backend/src/main/resources/application-prod.properties#L1-L84).