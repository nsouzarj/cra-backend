# CRA Backend - Spring Boot API

Este projeto é um backend desenvolvido em Spring Boot para o sistema CRA (Correspondente Responsável por Atos), baseado nas entidades do projeto original localizado em `D:\Projetos\cra\src\br\adv\cra\entity`.

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data JPA**
- **Spring Web**
- **Spring Security** (JWT Authentication)
- **Spring Validation**
- **JWT (JSON Web Tokens)**
- **PostgreSQL** (produção)
- **MySQL** (alternativo)
- **H2 Database** (desenvolvimento/testes)
- **Lombok**
- **Maven**
- **Swagger/OpenAPI 3.0** (Documentação da API)
- **Docker** (Containerização)

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- **PostgreSQL 12+** (produção) - Configurado em 192.168.1.105:5432
- MySQL 8.0 (alternativo)
- Docker (opcional, para containerização)

## 🛠️ Configuração do Projeto

### 1. Clone e Configuração Inicial

O projeto já está configurado na pasta atual. Para executar:

### 2. Configuração do Banco de Dados

#### Para Desenvolvimento (H2):
```bash
# Usar o perfil de desenvolvimento (padrão)
# O banco H2 será criado automaticamente em memória
```

#### Para Produção (PostgreSQL):
```sql
-- Conectar ao PostgreSQL como superusuário
psql -h 192.168.1.105 -U postgres

-- Criar banco de dados (se não existir)
CREATE DATABASE dbcra WITH ENCODING 'UTF8';

-- Verificar conexão
\c dbcra
\dt
```

#### Para MySQL (Alternativo):
```sql
-- Criar banco de dados
CREATE DATABASE cra_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Criar usuário
CREATE USER 'cra_user'@'localhost' IDENTIFIED BY 'cra_password';
GRANT ALL PRIVILEGES ON cra_db.* TO 'cra_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Execução do Projeto

#### Modo Produção (PostgreSQL):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Modo Desenvolvimento (H2):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Modo Padrão (PostgreSQL):
```bash
mvn spring-boot:run
```

### 4. Execução com Docker

O projeto inclui suporte completo para Docker. Para mais detalhes, consulte [DOCKER.md](DOCKER.md).

#### Execução com Docker Compose (Produção):
```bash
docker-compose up -d
```

#### Execução com Docker Compose (Desenvolvimento):
```bash
docker-compose -f docker-compose.dev.yml up -d
```

### 5. Acesso às Aplicações

- **API REST**: http://localhost:8080/cra-api
- **H2 Console** (dev): http://localhost:8080/cra-api/h2-console
- **Actuator Health**: http://localhost:8080/cra-api/actuator/health
- **Swagger UI**: http://localhost:8080/cra-api/swagger-ui.html
- **API Docs**: http://localhost:8080/cra-api/api-docs

## 📚 Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/adv/cra/
│   │   ├── CraBackendApplication.java
│   │   ├── config/
│   │   │   ├── WebConfig.java
│   │   │   ├── DatabaseLoader.java
│   │   │   ├── ContentNegotiationConfig.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── entity/
│   │   │   ├── Usuario.java
│   │   │   ├── Correspondente.java
│   │   │   ├── Endereco.java
│   │   │   ├── Uf.java
│   │   │   ├── Comarca.java
│   │   │   ├── Orgao.java
│   │   │   ├── Processo.java
│   │   │   ├── Solicitacao.java
│   │   │   ├── TipoSolicitacao.java
│   │   │   ├── ComarcaCorrespondente.java
│   │   │   ├── EmailCorrespondente.java
│   │   │   ├── AndamentoCPJ.java
│   │   │   ├── ArquivoAnexoCPRO.java
│   │   │   ├── ArquivoAnexoCPROSalvo.java
│   │   │   ├── ArquivoColaborador.java
│   │   │   ├── AuditoriaInterna.java
│   │   │   ├── BancaProcesso.java
│   │   │   ├── Banco.java
│   │   │   ├── ClienteJSON.java
│   │   │   ├── ComarcaPossui.java
│   │   │   ├── Envio.java
│   │   │   ├── Enviosolicitacao.java
│   │   │   ├── FormularioAudiencia.java
│   │   │   ├── FormularioAudienciaNovo.java
│   │   │   ├── GedFinanceiro.java
│   │   │   ├── HistArqCpro.java
│   │   │   ├── HistArqCproRejeitado.java
│   │   │   ├── Historico.java
│   │   │   ├── LogSistema.java
│   │   │   ├── OutraParteJSON.java
│   │   │   ├── PerfilUsuario.java
│   │   │   ├── Preposto.java
│   │   │   ├── ProcessoCPJ.java
│   │   │   ├── ProcessoCPPRO.java
│   │   │   ├── ProcessoCpproConsulta.java
│   │   │   ├── ProcessoJSON.java
│   │   │   ├── ReciboPagamento.java
│   │   │   ├── Rejeitado.java
│   │   │   ├── Renumeracao.java
│   │   │   ├── SmsSalvo.java
│   │   │   ├── SoliArquivo.java
│   │   │   ├── SolicitacaoAnexo.java
│   │   │   ├── SolicitacaoPossuiArquivo.java
│   │   │   ├── StatusSolicitacao.java
│   │   │   └── TipoSolicitacaoCorrespondente.java
│   │   ├── repository/
│   │   │   ├── UsuarioRepository.java
│   │   │   ├── CorrespondenteRepository.java
│   │   │   ├── EnderecoRepository.java
│   │   │   ├── UfRepository.java
│   │   │   ├── ComarcaRepository.java
│   │   │   ├── OrgaoRepository.java
│   │   │   ├── ProcessoRepository.java
│   │   │   ├── SolicitacaoRepository.java
│   │   │   ├── TipoSolicitacaoRepository.java
│   │   │   └── TipoSolicitacaoCorrespondenteRepository.java
│   │   ├── service/
│   │   │   ├── UsuarioService.java
│   │   │   ├── CorrespondenteService.java
│   │   │   ├── ProcessoService.java
│   │   │   ├── SolicitacaoService.java
│   │   │   ├── UfService.java
│   │   │   ├── ComarcaService.java
│   │   │   ├── OrgaoService.java
│   │   │   ├── EnderecoService.java
│   │   │   ├── TipoSolicitacaoService.java
│   │   │   └── DatabaseConnectionService.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── UsuarioController.java
│   │   │   ├── CorrespondenteController.java
│   │   │   ├── ProcessoController.java
│   │   │   ├── SolicitacaoController.java
│   │   │   ├── UfController.java
│   │   │   ├── ComarcaController.java
│   │   │   ├── OrgaoController.java
│   │   │   └── TipoSolicitacaoController.java
│   │   ├── dto/
│   │       ├── JwtResponse.java
│   │       ├── LoginRequest.java
│   │       ├── RefreshTokenRequest.java
│   │       └── RegisterRequest.java
│   │   ├── security/
│   │       ├── SecurityConfig.java
│   │       ├── JwtUtils.java
│   │       ├── AuthTokenFilter.java
│   │       ├── AuthEntryPointJwt.java
│   │       ├── UserDetailsImpl.java
│   │       └── UserDetailsServiceImpl.java
│   │   └── util/
│   │       └── PasswordGenerator.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       ├── application-test.properties
│       └── data.sql
└── docs/
    ├── controllers.md
    └── CONTROLLER_DOCUMENTATION_SUMMARY.md
```

## 📖 Documentação da API

A documentação completa da API está disponível em [docs/controllers.md](docs/controllers.md). Esta documentação inclui:

- Descrição detalhada de todos os controllers
- Endpoints disponíveis com métodos HTTP
- Requisitos de autenticação para cada endpoint
- Exemplos de requisições e respostas
- Tipos de usuários e permissões

Além disso, cada controller possui documentação JavaDoc detalhada no próprio código fonte.

### Documentação Swagger/OpenAPI

O projeto agora inclui documentação interativa da API usando Swagger/OpenAPI 3.0:

- **Swagger UI**: `http://localhost:8080/cra-api/swagger-ui.html`
- **API Docs JSON**: `http://localhost:8080/cra-api/api-docs`

A documentação foi implementada de forma separada dos controllers para manter o código limpo e organizado. Para mais detalhes sobre como a documentação foi implementada, consulte o guia [SWAGGER_GUIDE.md](SWAGGER_GUIDE.md).

### Controllers Documentados

1. **AuthController** (`/api/auth`) - Autenticação e autorização
2. **UsuarioController** (`/api/usuarios`) - Gerenciamento de usuários
3. **CorrespondenteController** (`/api/correspondentes`) - Gerenciamento de correspondentes
4. **ProcessoController** (`/api/processos`) - Gerenciamento de processos legais
5. **SolicitacaoController** (`/api/solicitacoes`) - Gerenciamento de solicitações
6. **UfController** (`/api/ufs`) - Listagem de estados brasileiros
7. **ComarcaController** (`/api/comarcas`) - Gerenciamento de comarcas
8. **OrgaoController** (`/api/orgaos`) - Gerenciamento de órgãos
9. **TipoSolicitacaoController** (`/api/tipos-solicitacao`) - Gerenciamento de tipos de solicitação

Para mais detalhes, consulte o arquivo [docs/CONTROLLER_DOCUMENTATION_SUMMARY.md](docs/CONTROLLER_DOCUMENTATION_SUMMARY.md).

## 🔐 Segurança JWT

O projeto utiliza **JWT (JSON Web Tokens)** para autenticação e autorização.

### Tipos de Usuário e Permissões:
- **Tipo 1 - ADMIN**: Acesso completo ao sistema
- **Tipo 2 - ADVOGADO**: Acesso a funcionalidades de advogados
- **Tipo 3 - CORRESPONDENTE**: Acesso limitado para correspondentes

### Endpoints de Autenticação:

#### `POST /api/auth/login`
Autentica um usuário e retorna JWT token.

**Request:**
```json
{
  "login": "admin",
  "senha": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "login": "admin",
  "nomeCompleto": "Administrador do Sistema",
  "emailPrincipal": "admin@cra.com.br",
  "tipo": 1,
  "roles": ["ROLE_ADMIN"],
  "expiresAt": "2024-08-29T14:30:00"
}
```

#### `POST /api/auth/refresh`
Renova o JWT token usando refresh token.

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### `POST /api/auth/register` 🔒 **ADMIN only**
Registra um novo usuário (apenas administradores).

**Request:**
```json
{
  "login": "novousuario",
  "senha": "senha123",
  "nomeCompleto": "Nome Completo",
  "emailPrincipal": "email@exemplo.com",
  "tipo": 2
}
```

#### `GET /api/auth/me`
Retorna informações do usuário autenticado.

#### `POST /api/auth/logout`
Realiza logout (remove token no cliente).

#### `GET /api/auth/validate`