# CRA Backend - Controller Documentation

This document provides comprehensive documentation for all REST controllers in the CRA Backend application. Each controller manages a specific domain entity and provides endpoints for CRUD operations and specialized business logic.

## Table of Contents
1. [AuthController](#authcontroller)
2. [UsuarioController](#usuariocontroller)
3. [CorrespondenteController](#correspondentecontroller)
4. [ProcessoController](#processocontroller)
5. [SolicitacaoController](#solicitacaocontroller)
6. [UfController](#ufcontroller)
7. [ComarcaController](#comarcacontroller)
8. [OrgaoController](#orgaocontroller)
9. [TipoSolicitacaoController](#tiposolicitacaocontroller)

## AuthController

**Base URL:** `/api/auth`

The AuthController handles authentication and authorization operations for the application. It provides endpoints for user login, registration, token refresh, and user information retrieval.

### Endpoints

| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|-------------------------|
| POST | `/login` | Authenticates a user and returns JWT tokens | No |
| POST | `/register` | Registers a new user (Admin only) | Yes (ADMIN) |
| POST | `/refresh` | Refreshes an expired JWT token | No |
| GET | `/me` | Retrieves information about the current user | Yes |
| POST | `/logout` | Logs out the current user | Yes |
| GET | `/validate` | Validates the current JWT token | Yes |
| GET | `/database-info` | Retrieves database connection information | No |
| POST | `/test-password` | Tests password hashing | No |
| POST | `/debug-jwt` | Debugs JWT token generation | No |

### Request/Response Examples

**Login Request:**
```json
{
  "username": "admin",
  "password": "password"
}
```

**Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

## UsuarioController

**Base URL:** `/api/usuarios`

The UsuarioController manages user accounts in the system. It provides full CRUD operations for users with role-based access control.

### Endpoints

| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|-------------------------|
| POST | `/` | Creates a new user | Yes (ADMIN) |
| PUT | `/{id}` | Updates an existing user | Yes |
| GET | `/` | Lists all users | Yes |
| GET | `/{id}` | Retrieves a user by ID | Yes |
| GET | `/ativos` | Lists only active users | Yes |
| GET | `/buscar/login/{login}` | Finds user by login | Yes |
| GET | `/buscar/nome` | Searches users by name | Yes |
| GET | `/buscar/tipo/{tipo}` | Finds users by type | Yes |
| DELETE | `/{id}` | Deletes a user | Yes (ADMIN) |
| PUT | `/{id}/inativar` | Deactivates a user | Yes (ADMIN) |
| PUT | `/{id}/ativar` | Activates a user | Yes (ADMIN) |

### User Types
- 0: Administrator
- 1: Lawyer (Advogado)
- 2: Correspondent (Correspondente)

## CorrespondenteController

**Base URL:** `/api/correspondentes`

The CorrespondenteController manages legal correspondents in the system. It provides full CRUD operations and specialized search capabilities.

### Endpoints

| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|-------------------------|
| POST | `/` | Creates a new correspondent | Yes |
| PUT | `/{id}` | Updates an existing correspondent | Yes |
| GET | `/` | Lists all correspondents | Yes |
| GET | `/{id}` | Retrieves a correspondent by ID | Yes |
| GET | `/ativos` | Lists only active correspondents | Yes |
| GET | `/buscar/nome` | Searches correspondents by name | Yes |
| GET | `/buscar/cpfcnpj/{cpfCnpj}` | Finds correspondent by CPF/CNPJ | Yes |
| GET | `/buscar/oab/{oab}` | Finds correspondent by OAB number | Yes |
| GET | `/buscar/tipo/{tipo}` | Finds correspondents by type | Yes |
| GET | `/regra1` | Lists correspondents with rule 1 | Yes |
| GET | `/regra2` | Lists correspondents with rule 2 | Yes |
| PUT | `/{id}/inativar` | Deactivates a correspondent | Yes |
| PUT | `/{id}/ativar` | Activates a correspondent | Yes |
| DELETE | `/{id}` | Deletes a correspondent | Yes |

## ProcessoController

**Base URL:** `/api/processos`

The ProcessoController manages legal processes in the system. It provides full CRUD operations and advanced search capabilities.

### Endpoints

| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|-------------------------|
| POST | `/` | Creates a new process | Yes |
| PUT | `/{id}` | Updates an existing process | Yes |
| GET | `/` | Lists all processes | Yes |
| GET | `/{id}` | Retrieves a process by ID | Yes |
| GET | `/buscar/numero/{numeroProcesso}` | Finds process by number | Yes |
| GET | `/buscar/numero-pesquisa` | Searches processes by number | Yes |
| GET | `/buscar/parte` | Searches processes by party name | Yes |
| GET | `/buscar/adverso` | Searches processes by opposing party | Yes |
| GET | `/buscar/status/{status}` | Finds processes by status | Yes |
| GET | `/buscar/assunto` | Searches processes by subject | Yes |
| GET | `/buscar/comarca/{comarcaId}` | Finds processes by court district | Yes |
| GET | `/buscar/orgao/{orgaoId}` | Finds processes by court | Yes |
| GET | `/estatisticas/status/{status}` | Counts processes by status | Yes |
| DELETE | `/{id}` | Deletes a process | Yes |

## SolicitacaoController

**Base URL:** `/api/solicitacoes`

The SolicitacaoController manages requests in the system. It provides full CRUD operations and specialized endpoints for request management.

### Endpoints

| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|-------------------------|
| POST | `/` | Creates a new request | Yes |
| PUT | `/{id}` | Updates an existing request | Yes |
| GET | `/` | Lists all requests | Yes |
| GET | `/{id}` | Retrieves a request by ID | Yes |
| GET | `/pendentes` | Lists pending requests | Yes |
| GET | `/concluidas` | Lists completed requests | Yes |
| GET | `/atrasadas` | Lists overdue requests | Yes |
| GET | `/pagas` | Lists paid requests | Yes |
| GET | `/nao-pagas` | Lists unpaid requests | Yes |
| GET | `/usuario/{usuarioId}` | Finds requests by user | Yes |
| GET | `/buscar/periodo` | Searches requests by date range | Yes |
| GET | `/buscar/texto` | Searches requests by text | Yes |
| GET | `/buscar/grupo/{grupo}` | Finds requests by group | Yes |
| GET | `/buscar/status/{status}` | Finds requests by status | Yes |
| PUT | `/{id}/concluir` | Marks request as completed | Yes |
| PUT | `/{id}/marcar-pago` | Marks request as paid | Yes |
| PUT | `/{id}/marcar-nao-pago` | Marks request as unpaid | Yes |
| GET | `/estatisticas/pendentes` | Counts pending requests | Yes |
| DELETE | `/{id}` | Deletes a request | Yes |

## UfController

**Base URL:** `/api/ufs`

The UfController manages Brazilian states (Unidades Federativas). It provides read-only endpoints for listing and searching states, primarily for frontend dropdowns.

### Endpoints

| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|-------------------------|
| GET | `/` | Lists all states ordered by name | No |
| GET | `/{id}` | Retrieves a state by ID | No |
| GET | `/sigla/{sigla}` | Retrieves a state by abbreviation | No |
| GET | `/buscar/nome` | Searches states by name | No |

### Response Format
```json
{
  "id": 1,
  "sigla": "SP",
  "nome": "São Paulo"
}
```

## ComarcaController

**Base URL:** `/api/comarcas`

The ComarcaController manages court districts (Comarcas) in the system. It provides full CRUD operations for managing court districts.

### Endpoints

| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|-------------------------|
| POST | `/` | Creates a new court district | Yes |
| PUT | `/{id}` | Updates an existing court district | Yes |
| GET | `/` | Lists all court districts | Yes |
| GET | `/{id}` | Retrieves a court district by ID | Yes |
| GET | `/ativos` | Lists only active court districts | Yes |
| GET | `/buscar/nome` | Searches court districts by name | Yes |
| PUT | `/{id}/inativar` | Deactivates a court district | Yes |
| PUT | `/{id}/ativar` | Activates a court district | Yes |
| DELETE | `/{id}` | Deletes a court district | Yes |

## OrgaoController

**Base URL:** `/api/orgaos`

The OrgaoController manages courts (Orgaos) in the system. It provides full CRUD operations for managing courts.

### Endpoints

| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|-------------------------|
| POST | `/` | Creates a new court | Yes |
| PUT | `/{id}` | Updates an existing court | Yes |
| GET | `/` | Lists all courts | Yes |
| GET | `/{id}` | Retrieves a court by ID | Yes |
| GET | `/ativos` | Lists only active courts | Yes |
| GET | `/buscar/nome` | Searches courts by name | Yes |
| PUT | `/{id}/inativar` | Deactivates a court | Yes |
| PUT | `/{id}/ativar` | Activates a court | Yes |
| DELETE | `/{id}` | Deletes a court | Yes |

## TipoSolicitacaoController

**Base URL:** `/api/tipos-solicitacao`

The TipoSolicitacaoController manages request types in the system. It provides full CRUD operations for managing request types.

### Endpoints

| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|-------------------------|
| POST | `/` | Creates a new request type | Yes |
| PUT | `/{id}` | Updates an existing request type | Yes |
| GET | `/` | Lists all request types | Yes |
| GET | `/{id}` | Retrieves a request type by ID | Yes |
| GET | `/ativos` | Lists only active request types | Yes |
| GET | `/buscar/nome` | Searches request types by name | Yes |
| PUT | `/{id}/inativar` | Deactivates a request type | Yes |
| PUT | `/{id}/ativar` | Activates a request type | Yes |
| DELETE | `/{id}` | Deletes a request type | Yes |