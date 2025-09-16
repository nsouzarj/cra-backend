# SoliArquivo Implementation

## Overview

This document describes the implementation of the new file attachment functionality for solicitations using the `SoliArquivo` entity. This implementation allows each solicitation to have multiple attached files with improved access control.

## Entity Structure

The `SoliArquivo` entity has the following structure:

- `id`: Primary key
- `solicitacao`: Reference to the associated solicitation
- `nomearquivo`: Name of the uploaded file
- `datainclusao`: Timestamp when the file was uploaded
- `caminhofisico`: Physical path where the file is stored
- `origem`: Source of the file (e.g., "correspondente" or "usuario")
- `ativo`: Boolean flag indicating if the file is active
- `caminhorelativo`: Relative HTTP path to access the file

## Key Features

1. **One-to-Many Relationship**: Each solicitation can have multiple attached files
2. **Access Control**: Correspondents can only delete their own files
3. **Flexible Storage**: Files are stored in a configurable directory
4. **RESTful API**: Complete CRUD operations via REST endpoints

## API Endpoints

- `POST /api/soli-arquivos/upload`: Upload a new file attachment
- `GET /api/soli-arquivos/solicitacao/{solicitacaoId}`: Get all files for a solicitation
- `GET /api/soli-arquivos/{id}`: Get a specific file by ID
- `PUT /api/soli-arquivos/{id}`: Update file information
- `DELETE /api/soli-arquivos/{id}`: Delete a file (with access control)

## Implementation Details

### Components Created

1. `SoliArquivo` entity
2. `SoliArquivoRepository` repository interface
3. `SoliArquivoService` service class with business logic
4. `SoliArquivoController` REST controller
5. `SoliArquivoDTO` data transfer object
6. `SoliArquivoMapper` utility for entity/DTO conversion
7. Unit tests for service and controller

### Access Control

The implementation includes access control logic where:
- Correspondents can only delete files they uploaded
- Administrators and other users can delete any file

## Configuration

The file storage directory is configured in `application.properties`:
```
file.upload-dir=D:\\Projetos\\craweb\\arquivos
```

## Usage Examples

### Upload a File
```bash
curl -X POST \
  http://localhost:8081/cra-api/api/soli-arquivos/upload \
  -H 'content-type: multipart/form-data' \
  -F 'file=@/path/to/file.pdf' \
  -F 'solicitacaoId=123' \
  -F 'origem=correspondente'
```

### List Files for a Solicitation
```bash
curl -X GET \
  http://localhost:8081/cra-api/api/soli-arquivos/solicitacao/123
```

### Delete a File
```bash
curl -X DELETE \
  http://localhost:8081/cra-api/api/soli-arquivos/456?origem=correspondente
```