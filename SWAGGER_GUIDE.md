# Guia de Documentação da API com Swagger

## Visão Geral

Este projeto utiliza o Swagger (OpenAPI 3.0) para documentar a API do CRA Backend. A documentação é implementada de forma separada dos controllers para manter o código limpo e organizado.

## Configuração

A configuração do Swagger foi implementada nas seguintes classes:

1. `SwaggerConfig.java` - Configuração principal do OpenAPI
2. `SwaggerDocumentationConfig.java` - Definição de tags e informações gerais
3. `ApiDocumentation.java` - Interface com anotações Swagger para documentar endpoints

## Dependências

As seguintes dependências foram adicionadas ao `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

## Endpoints do Swagger

Após iniciar a aplicação, você pode acessar os seguintes endpoints:

- **Swagger UI**: `http://localhost:8081/cra-api/swagger-ui.html`
- **API Docs JSON**: `http://localhost:8081/cra-api/api-docs`
- **API Docs YAML**: `http://localhost:8081/cra-api/v3/api-docs.yaml`

## Como Adicionar Documentação para Novos Endpoints

Para documentar novos endpoints sem modificar os controllers existentes:

1. Crie métodos na interface `ApiDocumentation.java` com as anotações apropriadas
2. Use as anotações do pacote `io.swagger.v3.oas.annotations`
3. Referencie os DTOs existentes nas anotações

### Exemplo de Documentação

```java
@Operation(
    summary = "Descrição breve da operação",
    description = "Descrição detalhada da operação"
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Sucesso", 
        content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = SeuDto.class))),
    @ApiResponse(responseCode = "400", description = "Erro de validação",
        content = @Content(mediaType = "application/json"))
})
ResponseEntity<?> nomeDoMetodo();
```

## Tags Disponíveis

As seguintes tags estão configuradas para organizar a documentação:

- `auth` - Operações de autenticação e autorização
- `comarca` - Operações relacionadas a comarcas
- `correspondente` - Operações relacionadas a correspondentes
- `orgao` - Operações relacionadas a órgãos
- `processo` - Operações relacionadas a processos
- `solicitacao` - Operações relacionadas a solicitações
- `status-solicitacao` - Operações relacionadas ao status de solicitações
- `tipo-solicitacao` - Operações relacionadas aos tipos de solicitações
- `uf` - Operações relacionadas às unidades federativas
- `usuario` - Operações relacionadas a usuários

## Segurança

A API utiliza autenticação JWT. Os endpoints protegidos são marcados com a anotação `@SecurityRequirement(name = "bearerAuth")`.

Para autenticar nas APIs protegidas através do Swagger UI:

1. Utilize o endpoint `/api/auth/login` para obter um token JWT
2. Copie o token retornado (sem as aspas)
3. Clique no botão "Authorize" no topo da página do Swagger UI
4. Insira o token no formato: `Bearer seu_token_aqui`
5. Clique em "Authorize" e depois em "Close"

## Personalização

As seguintes propriedades podem ser configuradas no `application.properties`:

```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
```

## Solução de Problemas

### Acesso não autorizado ao Swagger UI

Se você estiver recebendo erros de "Acesso não autorizado" ao tentar acessar o Swagger UI, verifique se as seguintes URLs estão configuradas para permitir acesso sem autenticação no arquivo `SecurityConfig.java`:

- `/swagger-ui/**`
- `/swagger-ui.html`
- `/api-docs/**`
- `/v3/api-docs/**`
- `/webjars/**`

### Problemas com autenticação JWT

Se estiver tendo problemas com a autenticação JWT nos endpoints protegidos:

1. Certifique-se de que o token está no formato correto: `Bearer seu_token_aqui`
2. Verifique se o token não expirou
3. Confirme se o usuário tem as permissões necessárias para acessar o endpoint