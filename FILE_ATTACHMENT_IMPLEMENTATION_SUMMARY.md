# File Attachment Implementation Summary

This document summarizes the implementation of file attachment functionality for solicitations in the CRA Backend system.

## Overview

The file attachment functionality allows users to upload, manage, and retrieve file attachments associated with solicitations. This feature is particularly useful when correspondents need to attach documents to solicitations via the frontend.

## Components Implemented

### 1. Entities

- **SolicitacaoAnexo**: Represents a file attachment with metadata such as filename, MIME type, and storage path
- **SolicitacaoPossuiArquivo**: Represents the many-to-many relationship between solicitations and file attachments
- **SolicitacaoPossuiArquivoId**: Composite key class for the relationship entity

### 2. Repositories

- **SolicitacaoAnexoRepository**: JPA repository for managing file attachments
- **SolicitacaoPossuiArquivoRepository**: JPA repository for managing relationships between solicitations and file attachments

### 3. Services

- **SolicitacaoAnexoService**: Business logic for handling file operations including:
  - Saving file attachments to the filesystem
  - Associating files with solicitations
  - Retrieving file attachments
  - Updating file attachment metadata
  - Deleting file attachments and their physical files

### 4. Controllers

- **SolicitacaoAnexoController**: REST endpoints for file attachment operations:
  - `POST /api/solicitacoes-anexos/upload` - Upload a file attachment
  - `GET /api/solicitacoes-anexos/solicitacao/{solicitacaoId}` - List file attachments for a solicitation
  - `GET /api/solicitacoes-anexos/{id}` - Get a specific file attachment
  - `PUT /api/solicitacoes-anexos/{id}` - Update a file attachment
  - `DELETE /api/solicitacoes-anexos/{id}` - Delete a file attachment

### 5. Configuration

- Added `file.upload-dir` property to `application.properties` for configuring the file storage directory

### 6. Tests

- **SolicitacaoAnexoServiceTest**: Unit tests for the file attachment service
- **SolicitacaoAnexoControllerTest**: Unit tests for the file attachment controller

### 7. Documentation

- **FILE_ATTACHMENT_API.md**: Detailed API documentation for file attachment endpoints
- Updated **README.md**: Added information about the new file attachment functionality

## Technical Details

### File Storage

Files are stored in the directory specified by the `file.upload-dir` property. Each file is given a unique name using UUID to prevent filename conflicts.

### Database Schema

The implementation uses two main tables:
1. `arquivosanexo` - Stores file attachment metadata
2. `solicitacao_possui_arquivo` - Junction table for the many-to-many relationship

### Security

File upload operations require authentication and automatically associate the uploaded file with the authenticated user.

## Usage Example

To upload a file attachment for a solicitation:

```bash
curl -X POST "http://localhost:8081/cra-api/api/solicitacoes-anexos/upload" \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/document.pdf" \
  -F "solicitacaoId=123"
```

To retrieve all file attachments for a solicitation:

```bash
curl -X GET "http://localhost:8081/cra-api/api/solicitacoes-anexos/solicitacao/123" \
  -H "Authorization: Bearer <token>"
```

## Future Improvements

1. Add file download endpoint
2. Implement file size limits
3. Add support for file type validation
4. Implement file preview/thumbnail generation
5. Add batch upload functionality