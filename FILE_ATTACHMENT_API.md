# File Attachment API for Solicitations

This document describes the API endpoints for managing file attachments in solicitations.

## Configuration

To use the file attachment functionality, you need to configure the file storage directory in your `application.properties`:

```properties
file.upload-dir=./uploads
```

Make sure this directory exists and is writable by the application.

## Endpoints

### Upload a File Attachment

Uploads a file and associates it with a specific solicitation.

**URL**: `POST /api/solicitacoes-anexos/upload`

**Parameters**:
- `file` (multipart/form-data): The file to upload
- `solicitacaoId` (Long): The ID of the solicitation to associate the file with

**Response**:
- `201 Created`: File uploaded successfully
- `404 Not Found`: Solicitation not found
- `500 Internal Server Error`: Upload failed

**Example Request**:
```bash
curl -X POST "http://localhost:8081/cra-api/api/solicitacoes-anexos/upload" \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/file.pdf" \
  -F "solicitacaoId=1"
```

### List File Attachments for a Solicitation

Retrieves all file attachments associated with a specific solicitation.

**URL**: `GET /api/solicitacoes-anexos/solicitacao/{solicitacaoId}`

**Path Parameters**:
- `solicitacaoId` (Long): The ID of the solicitation

**Response**:
- `200 OK`: Returns a list of file attachments
- `500 Internal Server Error`: Retrieval failed

**Example Request**:
```bash
curl -X GET "http://localhost:8081/cra-api/api/solicitacoes-anexos/solicitacao/1" \
  -H "Authorization: Bearer <token>"
```

### Get a Specific File Attachment

Retrieves information about a specific file attachment.

**URL**: `GET /api/solicitacoes-anexos/{id}`

**Path Parameters**:
- `id` (Long): The ID of the file attachment

**Response**:
- `200 OK`: Returns the file attachment information
- `404 Not Found`: File attachment not found
- `500 Internal Server Error`: Retrieval failed

**Example Request**:
```bash
curl -X GET "http://localhost:8081/cra-api/api/solicitacoes-anexos/1" \
  -H "Authorization: Bearer <token>"
```

### Update a File Attachment

Updates information about a specific file attachment.

**URL**: `PUT /api/solicitacoes-anexos/{id}`

**Path Parameters**:
- `id` (Long): The ID of the file attachment

**Request Body**:
```json
{
  "nomearquivo": "updated-file-name.pdf",
  "tipoarquivo": "application/pdf",
  "operacao": "Entrada",
  "origemarq": 1
}
```

**Response**:
- `200 OK`: File attachment updated successfully
- `404 Not Found`: File attachment not found
- `500 Internal Server Error`: Update failed

**Example Request**:
```bash
curl -X PUT "http://localhost:8081/cra-api/api/solicitacoes-anexos/1" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"nomearquivo":"updated-file-name.pdf","tipoarquivo":"application/pdf"}'
```

### Delete a File Attachment

Deletes a specific file attachment and removes the physical file from storage.

**URL**: `DELETE /api/solicitacoes-anexos/{id}`

**Path Parameters**:
- `id` (Long): The ID of the file attachment

**Response**:
- `204 No Content`: File attachment deleted successfully
- `404 Not Found`: File attachment not found
- `500 Internal Server Error`: Deletion failed

**Example Request**:
```bash
curl -X DELETE "http://localhost:8081/cra-api/api/solicitacoes-anexos/1" \
  -H "Authorization: Bearer <token>"
```

## Entity Structure

### SolicitacaoAnexo

Represents a file attachment.

**Fields**:
- `idarquivoanexo` (Long): Unique identifier
- `nomearquivo` (String): Original filename
- `tipoarquivo` (String): MIME type of the file
- `linkarquivo` (String): Path to the physical file
- `datasolicitacao` (LocalDateTime): Upload timestamp
- `operacao` (String): Operation type (Entrada/Saida)
- `origemarq` (Integer): Source of the file (1-Siegecol, 2-Cprpo)
- `usuario` (Usuario): User who uploaded the file

### SolicitacaoPossuiArquivo

Represents the relationship between solicitations and file attachments.

**Fields**:
- `solicitacao` (Solicitacao): The solicitation
- `solicitacaoAnexo` (SolicitacaoAnexo): The file attachment

## Implementation Details

The file attachment functionality is implemented through the following components:

1. **SolicitacaoAnexoRepository**: JPA repository for file attachments
2. **SolicitacaoPossuiArquivoRepository**: JPA repository for the relationship between solicitations and file attachments
3. **SolicitacaoAnexoService**: Business logic for managing file attachments
4. **SolicitacaoAnexoController**: REST endpoints for file attachment operations

Files are stored in the directory specified by the `file.upload-dir` property with unique filenames to prevent conflicts.