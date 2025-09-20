package br.adv.cra.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(
        title = "CRA Backend API",
        version = "1.0",
        description = "Documentação da API do Backend do CRA - Sistema de Gerenciamento de Correspondentes Responsáveis por Atos"
    ),
    servers = {
        @Server(url = "/cra-api", description = "Servidor de desenvolvimento")
    },
    tags = {
        @Tag(name = "auth", description = "Operações de autenticação e autorização\n\n" +
            "Endpoints para login, registro, validação e gerenciamento de tokens JWT. " +
            "Esses endpoints permitem que os usuários autentiquem-se no sistema e gerenciem suas sessões."),
        @Tag(name = "comarca", description = "Operações relacionadas a comarcas\n\n" +
            "Endpoints para gerenciar comarcas (jurisdições territoriais onde atuam os juízes). " +
            "Permite criar, ler, atualizar e deletar informações sobre comarcas."),
        @Tag(name = "correspondente", description = "Operações relacionadas a correspondentes\n\n" +
            "Endpoints para gerenciar correspondentes jurídicos. " +
            "Permite criar, ler, atualizar e deletar informações sobre correspondentes, " +
            "bem como associá-los a comarcas e tipos de solicitação."),
        @Tag(name = "orgao", description = "Operações relacionadas a órgãos\n\n" +
            "Endpoints para gerenciar órgãos públicos (como tribunais, varas, promotorias). " +
            "Permite criar, ler, atualizar e deletar informações sobre órgãos."),
        @Tag(name = "processo", description = "Operações relacionadas a processos\n\n" +
            "Endpoints para gerenciar processos judiciais. " +
            "Permite criar, ler, atualizar e deletar informações sobre processos, " +
            "bem como associá-los a órgãos e comarcas."),
        @Tag(name = "solicitacao", description = "Operações relacionadas a solicitações\n\n" +
            "Endpoints para gerenciar solicitações de serviços jurídicos. " +
            "Permite criar, ler, atualizar e deletar solicitações, " +
            "bem como associá-las a processos, usuários e comarcas."),
        @Tag(name = "status-solicitacao", description = "Operações relacionadas ao status de solicitações\n\n" +
            "Endpoints para gerenciar os status possíveis para solicitações. " +
            "Status padrão incluem: Pendente, Em Andamento, Concluída e Cancelada."),
        @Tag(name = "tipo-solicitacao", description = "Operações relacionadas aos tipos de solicitações\n\n" +
            "Endpoints para gerenciar os tipos de solicitações disponíveis no sistema. " +
            "Exemplos incluem: Protesto, Cobrança, Notificação, Audiência e Diligência."),
        @Tag(name = "uf", description = "Operações relacionadas às unidades federativas\n\n" +
            "Endpoints para gerenciar unidades federativas (estados e Distrito Federal). " +
            "Permite criar, ler, atualizar e deletar informações sobre estados."),
        @Tag(name = "usuario", description = "Operações relacionadas a usuários\n\n" +
            "Endpoints para gerenciar usuários do sistema. " +
            "Permite criar, ler, atualizar e deletar informações sobre usuários, " +
            "bem como gerenciar seus perfis e permissões."),
        @Tag(name = "soli-arquivo", description = "Operações relacionadas a arquivos anexados às solicitações\n\n" +
            "Endpoints para gerenciar arquivos anexados às solicitações. " +
            "Permite fazer upload, download, listar, atualizar e deletar arquivos, " +
            "com suporte a armazenamento local ou no Google Drive."),
        @Tag(name = "google-drive", description = "Operações relacionadas à integração com Google Drive\n\n" +
            "Endpoints para gerenciar a integração com o Google Drive. " +
            "Permite conectar, desconectar e verificar o status da conta do Google Drive do usuário, " +
            "bem como fazer upload e download de arquivos diretamente do Google Drive.")
    }
)
public class SwaggerDocumentationConfig {
    // Esta classe serve apenas para configuração da documentação Swagger
}