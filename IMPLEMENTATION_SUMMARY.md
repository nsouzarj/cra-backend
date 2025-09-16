# CRA Backend Implementation Summary

## Overview
This document provides a comprehensive summary of the CRA Backend implementation, including all entities, repositories, services, controllers, and their relationships.

## Project Structure

### Configuration
```
src/main/java/br/adv/cra/config/
├── ApiDocumentation.java
├── ContentNegotiationConfig.java
├── CustomLocalDateTimeDeserializer.java
├── CustomLocalDateTimeSerializer.java
├── DatabaseLoader.java
├── GlobalExceptionHandler.java
├── JacksonConfig.java
├── SwaggerConfig.java
├── SwaggerDocumentationConfig.java
└── WebConfig.java
```

### Controllers
```
src/main/java/br/adv/cra/controller/
├── AuthController.java
├── ComarcaController.java
├── CorrespondenteController.java
├── OrgaoController.java
├── ProcessoController.java
├── SoliArquivoController.java
├── SolicitacaoController.java
├── StatusSolicitacaoController.java
├── TipoSolicitacaoController.java
├── UfController.java
└── UsuarioController.java
```

### DTOs
```
src/main/java/br/adv/cra/dto/
├── JwtResponse.java
├── LoginRequest.java
├── PasswordChangeRequest.java
├── ProcessoDTO.java
├── RefreshTokenRequest.java
├── RegisterRequest.java
└── SoliArquivoDTO.java
```

### Entities
```
src/main/java/br/adv/cra/entity/
├── AndamentoCPJ.java
├── ArquivoAnexoCPRO.java
├── ArquivoAnexoCPROSalvo.java
├── ArquivoColaborador.java
├── AuditoriaInterna.java
├── BancaProcesso.java
├── Banco.java
├── ClienteJSON.java
├── Comarca.java
├── ComarcaCorrespondente.java
├── ComarcaPossui.java
├── Correspondente.java
├── EmailCorrespondente.java
├── Endereco.java
├── Envio.java
├── Enviosolicitacao.java
├── FormularioAudiencia.java
├── FormularioAudienciaNovo.java
├── GedFinanceiro.java
├── HistArqCproRejeitado.java
├── Historico.java
├── LogSistema.java
├── Orgao.java
├── OutraParteJSON.java
├── PerfilUsuario.java
├── Preposto.java
├── Processo.java
├── ProcessoCPJ.java
├── ProcessoCPPRO.java
├── ProcessoCpproConsulta.java
├── ProcessoJSON.java
├── ReciboPagamento.java
├── Rejeitado.java
├── Renumeracao.java
├── SmsSalvo.java
├── SoliArquivo.java
├── Solicitacao.java
├── StatusSolicitacao.java
├── TipoSolicitacao.java
├── TipoSolicitacaoCorrespondente.java
├── Uf.java
└── Usuario.java
```

### Repositories
```
src/main/java/br/adv/cra/repository/
├── AndamentoCPJRepository.java
├── ArquivoAnexoCPRORepository.java
├── ArquivoAnexoCPROSalvoRepository.java
├── ComarcaCorrespondenteRepository.java
├── ComarcaPossuiRepository.java
├── ComarcaRepository.java
├── CorrespondenteRepository.java
├── EmailCorrespondenteRepository.java
├── EnvioRepository.java
├── FormularioAudienciaNovoRepository.java
├── FormularioAudienciaRepository.java
├── GedFinanceiroRepository.java
├── HistArqCproRejeitadoRepository.java
├── HistoricoRepository.java
├── OrgaoRepository.java
├── PrepostoRepository.java
├── ProcessoCPJRepository.java
├── ProcessoCPPRORepository.java
├── ProcessoRepository.java
├── ReciboPagamentoRepository.java
├── RenumeracaoRepository.java
├── SoliArquivoRepository.java
├── SolicitacaoRepository.java
├── StatusSolicitacaoRepository.java
├── TipoSolicitacaoCorrespondenteRepository.java
├── TipoSolicitacaoRepository.java
├── UfRepository.java
└── UsuarioRepository.java
```

### Security
```
src/main/java/br/adv/cra/security/
├── AuthTokenFilter.java
├── CorsConfig.java
├── CustomUserDetailsService.java
├── JwtAuthEntryPoint.java
├── JwtUtils.java
├── SecurityConfig.java
└── WebSecurityConfig.java
```

### Services
```
src/main/java/br/adv/cra/service/
├── AuthService.java
├── ComarcaService.java
├── CorrespondenteService.java
├── DatabaseConnectionService.java
├── EnderecoService.java
├── OrgaoService.java
├── ProcessoService.java
├── SoliArquivoService.java
├── SolicitacaoService.java
├── StatusSolicitacaoService.java
├── TipoSolicitacaoService.java
├── UfService.java
└── UsuarioService.java
```

### Utilities
```
src/main/java/br/adv/cra/util/
├── PasswordHashGenerator.java
├── SoliArquivoMapper.java
└── TestSerialization.java
```

### Tests
```
src/test/java/br/adv/cra/
├── controller/
│   ├── AuthControllerIntegrationTest.java
│   ├── AuthControllerUnitTest.java
│   ├── ComarcaControllerTest.java
│   ├── CorrespondenteControllerTest.java
│   ├── SoliArquivoControllerTest.java
│   └── UfControllerTest.java
├── service/
│   ├── AuthServiceTest.java
│   ├── SoliArquivoServiceTest.java
│   ├── SolicitacaoServiceTest.java
│   └── UsuarioServiceTest.java
└── util/
    └── PasswordHashGenerator.java
```

## Key Entities and Relationships

### Usuario
Represents system users with authentication and authorization capabilities.

### Solicitacao
Central entity representing legal process requests with relationships to:
- Processo (legal process)
- StatusSolicitacao (request status)
- Usuario (requesting user)
- Correspondente (assigned correspondent)
- And multiple other entities for comprehensive process management

### SoliArquivo
Represents file attachments for solicitations with:
- Direct one-to-many relationship with Solicitacao
- File storage with configurable path
- Access control (correspondents can only delete their own files)
- Metadata including upload timestamp, origin, and active status

### Processo
Legal process entity with relationships to multiple solicitations.

## New File Attachment Implementation (SoliArquivo)

### SoliArquivo
The new file attachment implementation uses a simplified approach:
- Direct relationship with Solicitacao (one-to-many)
- Fields for file metadata and access control
- Simplified storage and retrieval

### SoliArquivoRepository
JPA repository for managing SoliArquivo entities.

### SoliArquivoService
Business logic for file operations:
- File upload with unique naming
- Configurable storage directory
- Access control implementation
- File retrieval and deletion

### SoliArquivoController
REST endpoints for file attachment operations:
- Upload, retrieval, update, and deletion
- Proper HTTP status codes and error handling

### SoliArquivoDTO
Data Transfer Object for API responses containing only necessary information.

### SoliArquivoMapper
Utility class for converting between entities and DTOs.

## Removed Components
The following components from the old file attachment implementation have been removed:
- SolicitacaoAnexo (file attachment entity)
- SolicitacaoAnexoRepository (repository for file attachments)
- SolicitacaoAnexoService (business logic for file operations)
- SolicitacaoAnexoController (REST endpoints for file operations)
- SolicitacaoPossuiArquivo (many-to-many relationship entity)
- SolicitacaoPossuiArquivoId (composite key class)
- SolicitacaoPossuiArquivoRepository (repository for relationships)
- HistArqCpro (file history entity)

These components were part of a more complex implementation that used a many-to-many relationship between solicitations and file attachments. The new implementation uses a simpler one-to-many relationship where each file attachment belongs to exactly one solicitation.

## Configuration
The application is configured through property files:
- application.properties (main configuration)
- application-dev.properties (development profile)
- application-prod.properties (production profile)
- application-test.properties (test profile)

Key configuration includes:
- Database connections
- File storage directory (file.upload-dir)
- JWT secret and expiration
- Server port and context path

## Security
JWT-based authentication and authorization with role-based access control:
- ADMIN: Full system access
- ADVOGADO: Lawyer access to relevant processes
- CORRESPONDENTE: Correspondent access to assigned processes

## Testing
Comprehensive unit tests for controllers and services ensure functionality and prevent regressions.

## API Documentation
Swagger/OpenAPI documentation available at `/swagger-ui.html` provides interactive API exploration.