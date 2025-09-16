# File Attachment API

## Overview
This document describes the file attachment functionality in the CRA Backend system. The API allows users to attach files to solicitations with proper access control.

## New Implementation (SoliArquivo)

### SoliArquivo
The new implementation uses a simplified approach with the SoliArquivo entity:

#### Fields
- `id` (Long): Primary key
- `solicitacao` (Solicitacao): Reference to the associated solicitation
- `nomearquivo` (String): Name of the uploaded file
- `datainclusao` (LocalDateTime): Timestamp when the file was uploaded
- `caminhofisico` (String): Physical path where the file is stored
- `origem` (String): Source of the file (e://d:\Projetos\craweb\cra-backend\src\main\java\br\adv\cra\entity\SoliArquivo.java#L13-L19g., "correspondente" or "usuario")
- `ativo` (boolean): Boolean flag indicating if the file is active
- `caminhorelativo` (String): Relative HTTP path to access the file

### SoliArquivoRepository
JPA repository for managing SoliArquivo entities with the following methods:
- Standard CRUD operations inherited from JpaRepository
- `findBySolicitacao(Solicitacao solicitacao)`: Find all files for a solicitation
- `findBySolicitacaoIdsolicitacao(Long idSolicitacao)`: Find all files for a solicitation by ID

### SoliArquivoService
Business logic for handling file operations including:

#### Methods
- `salvarAnexo(MultipartFile file, Long solicitacaoId, String origem)`: Save a new file attachment
- `listarAnexosPorSolicitacao(Long solicitacaoId)`: Get all files for a solicitation
- `buscarPorId(Long id)`: Get a specific file by ID
- `atualizar(Long id, SoliArquivo soliArquivo)`: Update file information
- `deletar(Long id)`: Delete a file
- `podeDeletar(Long id, String origem)`: Check if a file can be deleted by a specific origin

### SoliArquivoController
REST endpoints for file attachment operations:

#### Endpoints
- `POST /api/soli-arquivos/upload`: Upload a new file attachment
  - Parameters:
    - `file` (MultipartFile): The file to upload
    - `solicitacaoId` (Long): The ID of the solicitation to attach the file to
    - `origem` (String, optional): The origin of the file (default: "usuario")
  - Response: Created SoliArquivoDTO

- `GET /api/soli-arquivos/solicitacao/{solicitacaoId}`: Get all files for a solicitation
  - Parameters:
    - `solicitacaoId` (Long): The ID of the solicitation
  - Response: List of SoliArquivoDTO

- `GET /api/soli-arquivos/{id}`: Get a specific file by ID
  - Parameters:
    - `id` (Long): The ID of the file
  - Response: SoliArquivoDTO

- `PUT /api/soli-arquivos/{id}`: Update file information
  - Parameters:
    - `id` (Long): The ID of the file
    - `dto` (SoliArquivoDTO): The updated file information
  - Response: Updated SoliArquivoDTO

- `DELETE /api/soli-arquivos/{id}`: Delete a file (with access control)
  - Parameters:
    - `id` (Long): The ID of the file
    - `origem` (String, optional): The origin trying to delete the file (default: "usuario")
  - Response: 204 No Content

### SoliArquivoDTO
Data Transfer Object for API responses containing:
- `id` (Long): Primary key
- `idSolicitacao` (Long): ID of the associated solicitation
- `nomearquivo` (String): Name of the uploaded file
- `dataInclusao` (LocalDateTime): Timestamp when the file was uploaded
- `origem` (String): Source of the file
- `ativo` (boolean): Active status
- `caminhoRelativo` (String): Relative HTTP path to access the file

## API Usage Examples

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

### Get a Specific File
```bash
curl -X GET \
  http://localhost:8081/cra-api/api/soli-arquivos/456
```

### Update File Information
```bash
curl -X PUT \
  http://localhost:8081/cra-api/api/soli-arquivos/456 \
  -H 'content-type: application/json' \
  -d '{
    "id": 456,
    "idSolicitacao": 123,
    "nomearquivo": "updated-file.pdf",
    "origem": "usuario",
    "ativo": true,
    "caminhoRelativo": "/arquivos/updated-file.pdf"
  }'
```

### Delete a File
```bash
curl -X DELETE \
  http://localhost:8081/cra-api/api/soli-arquivos/456?origem=correspondente
```

## Access Control
The implementation includes access control logic where:
- Correspondents can only delete files they uploaded
- Administrators and other users can delete any file

This is implemented in the `podeDeletar` method in `SoliArquivoService`.

## Configuration
The file storage directory is configured in `application.properties`:
```
file.upload-dir=D:\Projetos\craweb\arquivos
```