# Integração Simplificada com Google Drive

## Visão Geral

Esta documentação descreve a integração simplificada com o Google Drive que foi implementada para o seu projeto. Todas as referências a credenciais armazenadas e userId foram removidas conforme solicitado.

## Arquitetura Atual

### Componentes Principais

1. **GoogleDriveService**: Serviço principal que lida com todas as operações do Google Drive
2. **GoogleDriveController**: Controlador REST para endpoints relacionados ao Google Drive
3. **SoliArquivoService**: Serviço que gerencia arquivos e pode usar o Google Drive como armazenamento

### Remoção de Componentes

Os seguintes componentes foram removidos pois não são mais necessários:
- `GoogleDriveCredential` entity
- `GoogleDriveCredentialRepository` repository

## Funcionalidades

### Upload de Arquivos
```java
String fileId = googleDriveService.uploadFile(MultipartFile file);
```

### Download de Arquivos
```java
InputStream fileStream = googleDriveService.downloadFile(String fileId);
```

### Exclusão de Arquivos
```java
googleDriveService.deleteFile(String fileId);
```

## Configuração

### application.properties
```properties
# GOOGLE DRIVE OAUTH CONFIGURATION
google.drive.oauth.enabled=true
google.drive.oauth.client.id=514473695163-8cq4j925224rtln7e43m7cb4892g7bkt.apps.googleusercontent.com
google.drive.oauth.client.secret=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
google.drive.folder.id=1rP56ReCpHnQqbmIKWfzJgK_m01EH7_G-
google.drive.oauth.redirect.uri=http://localhost:8081/cra-api/api/google-drive/callback
```

## Endpoints REST

### Obter URL de Autorização
```
GET /api/google-drive/authorize
```
Retorna a URL de autorização do Google para configurar a integração.

### Verificar Status
```
GET /api/google-drive/status
```
Verifica se o serviço do Google Drive está funcionando.

## Como Usar

### 1. Configuração Inicial
1. Certifique-se de que as credenciais OAuth2 estão configuradas corretamente no `application.properties`
2. Verifique se a API do Google Drive está habilitada no Google Cloud Console

### 2. Testar a Conexão
```bash
curl http://localhost:8081/cra-api/api/google-drive/status
```

### 3. Obter URL de Autorização
```bash
curl http://localhost:8081/cra-api/api/google-drive/authorize
```

### 4. Upload de Arquivos
Ao salvar um arquivo, você pode escolher armazená-lo localmente ou no Google Drive:
```bash
curl -X POST "http://localhost:8081/cra-api/soli-arquivos/upload" \
  -F "file=@meu-arquivo.pdf" \
  -F "solicitacaoId=1" \
  -F "storageLocation=google_drive"
```

## Tratamento de Erros

O serviço inclui tratamento robusto de erros:
- Timeouts para operações de rede
- Fallback automático para armazenamento local em caso de falhas
- Logging detalhado para diagnóstico de problemas

## Segurança

- As credenciais OAuth2 são carregadas a partir das variáveis de ambiente/configuração
- Não há armazenamento de tokens de acesso no banco de dados
- Todos os dados sensíveis devem ser protegidos adequadamente

## Solução de Problemas

### Se o upload para o Google Drive falhar:
1. Verifique a conexão com a internet
2. Confirme que as credenciais OAuth2 estão corretas
3. Verifique se a API do Google Drive está habilitada
4. O sistema fará fallback automaticamente para armazenamento local

### Se os endpoints retornarem erros:
1. Verifique os logs da aplicação para mensagens de erro detalhadas
2. Confirme que o serviço do Google Drive está acessível
3. Verifique as configurações de rede e firewall

## Notas Importantes

- Esta implementação não requer armazenamento de credenciais de usuário
- Todos os arquivos são acessados usando as credenciais da aplicação
- O fallback para armazenamento local é automático em caso de falhas
- Não há associação entre usuários e credenciais do Google Drive