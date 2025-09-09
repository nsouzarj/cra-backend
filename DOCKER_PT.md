# Configuração Docker para CRA Backend

Este documento explica como containerizar e executar a aplicação CRA Backend utilizando o Docker.

## Pré-requisitos

- Docker instalado no seu sistema
- Docker Compose (incluído com o Docker Desktop para Windows/Mac)

## Imagens Docker

O projeto inclui os seguintes arquivos de configuração Docker:

1. `Dockerfile` - Build multi-estágio para a aplicação Spring Boot
2. `docker-compose.yml` - Configuração de produção com banco de dados PostgreSQL
3. `docker-compose.dev.yml` - Configuração de desenvolvimento com banco de dados H2

## Construindo a Imagem Docker

Para construir a imagem Docker manualmente:

```bash
docker build -t cra-backend .
```

## Executando com o Docker Compose

### Ambiente de Produção (com PostgreSQL)

Para executar a aplicação com o banco de dados PostgreSQL:

```bash
docker-compose up -d
```

Isto irá iniciar:
- Banco de dados PostgreSQL na porta 5432
- Aplicação CRA Backend na porta 8081

Acesse a aplicação em: `http://localhost:8081/cra-api`

### Ambiente de Desenvolvimento (com H2)

Para executar a aplicação com o banco de dados H2 em memória:

```bash
docker-compose -f docker-compose.dev.yml up -d
```

Isto irá iniciar apenas a aplicação CRA Backend com o banco de dados H2.

Acesse a aplicação em: `http://localhost:8081/cra-api`

## Parando os Serviços

Para parar os serviços:

```bash
# Para configuração de produção
docker-compose down

# Para configuração de desenvolvimento
docker-compose -f docker-compose.dev.yml down
```

Para parar e remover volumes (incluindo dados do banco de dados):

```bash
# Para configuração de produção
docker-compose down -v

# Para configuração de desenvolvimento
docker-compose -f docker-compose.dev.yml down -v
```

## Acessando a Aplicação

Uma vez que os serviços estejam em execução, você pode acessar:

1. **Endpoints da API**: `http://localhost:8081/cra-api/api/`
2. **Swagger UI**: `http://localhost:8081/cra-api/swagger-ui.html`
3. **Documentação da API**: `http://localhost:8081/cra-api/api-docs`
4. **Health Check do Actuator**: `http://localhost:8081/cra-api/actuator/health`

## Credenciais de Usuários Padrão

A aplicação vem com usuários pré-configurados:

- **Administrador**: 
  - Nome de usuário: `admin`
  - Senha: `admin123`

- **Advogado**: 
  - Nome de usuário: `advogado1`
  - Senha: `adv123`

- **Correspondente**: 
  - Nome de usuário: `corresp1`
  - Senha: `corresp123`

## Variáveis de Ambiente

Você pode personalizar o comportamento da aplicação usando variáveis de ambiente:

- `SPRING_PROFILES_ACTIVE` - Definido como `dev`, `prod`, ou `test`
- `SPRING_DATASOURCE_URL` - URL de conexão com o banco de dados
- `SPRING_DATASOURCE_USERNAME` - Nome de usuário do banco de dados
- `SPRING_DATASOURCE_PASSWORD` - Senha do banco de dados
- `SERVER_PORT` - Porta da aplicação (padrão: 8081)
- `APP_JWT_SECRET` - Chave secreta JWT

## Solução de Problemas

### Problemas de Conexão com o Banco de Dados

Se você estiver tendo problemas para conectar ao banco de dados, certifique-se de:
1. O contêiner do PostgreSQL está em execução: `docker-compose ps`
2. As credenciais do banco de dados correspondem às da configuração da aplicação
3. A configuração de rede permite a comunicação entre os contêineres

### Logs da Aplicação

Para visualizar os logs da aplicação:

```bash
# Para configuração de produção
docker-compose logs cra-backend

# Para configuração de desenvolvimento
docker-compose -f docker-compose.dev.yml logs cra-backend-dev
```

### Reconstruindo a Aplicação

Se você fizer alterações no código, reconstrua os contêineres:

```bash
# Para configuração de produção
docker-compose build

# Para configuração de desenvolvimento
docker-compose -f docker-compose.dev.yml build
```