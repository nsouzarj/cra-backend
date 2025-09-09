# Docker Setup for CRA Backend

This document explains how to containerize and run the CRA Backend application using Docker.

## Prerequisites

- Docker installed on your system
- Docker Compose (included with Docker Desktop for Windows/Mac)

## Docker Images

The project includes the following Docker configuration files:

1. `Dockerfile` - Multi-stage build for the Spring Boot application
2. `docker-compose.yml` - Production setup with PostgreSQL database
3. `docker-compose.dev.yml` - Development setup with H2 in-memory database

## Building the Docker Image

To build the Docker image manually:

```bash
docker build -t cra-backend .
```

## Running with Docker Compose

### Production Environment (with PostgreSQL)

To run the application with PostgreSQL database:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5432
- CRA Backend application on port 8081

Access the application at: `http://localhost:8081/cra-api`

### Development Environment (with H2)

To run the application with H2 in-memory database:

```bash
docker-compose -f docker-compose.dev.yml up -d
```

This will start only the CRA Backend application with H2 database.

Access the application at: `http://localhost:8081/cra-api`

## Stopping the Services

To stop the services:

```bash
# For production setup
docker-compose down

# For development setup
docker-compose -f docker-compose.dev.yml down
```

To stop and remove volumes (including database data):

```bash
# For production setup
docker-compose down -v

# For development setup
docker-compose -f docker-compose.dev.yml down -v
```

## Accessing the Application

Once the services are running, you can access:

1. **API Endpoints**: `http://localhost:8081/cra-api/api/`
2. **Swagger UI**: `http://localhost:8081/cra-api/swagger-ui.html`
3. **API Docs**: `http://localhost:8081/cra-api/api-docs`
4. **Actuator Health**: `http://localhost:8081/cra-api/actuator/health`

## Default User Credentials

The application comes with pre-configured users:

- **Administrator**: 
  - Username: `admin`
  - Password: `admin123`

- **Lawyer**: 
  - Username: `advogado1`
  - Password: `adv123`

- **Correspondent**: 
  - Username: `corresp1`
  - Password: `corresp123`

## Environment Variables

You can customize the application behavior using environment variables:

- `SPRING_PROFILES_ACTIVE` - Set to `dev`, `prod`, or `test`
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SERVER_PORT` - Application port (default: 8081)
- `APP_JWT_SECRET` - JWT secret key

## Troubleshooting

### Database Connection Issues

If you're having issues connecting to the database, ensure:
1. The PostgreSQL container is running: `docker-compose ps`
2. The database credentials match those in the application configuration
3. The network configuration allows communication between containers

### Application Logs

To view application logs:

```bash
# For production setup
docker-compose logs cra-backend

# For development setup
docker-compose -f docker-compose.dev.yml logs cra-backend-dev
```

### Rebuilding the Application

If you make changes to the code, rebuild the containers:

```bash
# For production setup
docker-compose build

# For development setup
docker-compose -f docker-compose.dev.yml build
```

---

## 🌍 Documentação em Português

Este documento também está disponível em português: [DOCKER_PT.md](DOCKER_PT.md)