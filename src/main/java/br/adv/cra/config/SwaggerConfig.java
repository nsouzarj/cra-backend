package br.adv.cra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("/cra-api").description("Servidor de desenvolvimento")
                ))
                .info(new Info()
                        .title("CRA Backend API")
                        .version("1.0")
                        .description("Documentação da API do Backend do CRA - Sistema de Gerenciamento de Correspondentes Responsáveis por Atos\n\n" +
                                   "## Descrição\n" +
                                   "Esta API fornece endpoints para gerenciar usuários, correspondentes, processos e solicitações em um sistema jurídico.\n\n" +
                                   "## Autenticação\n" +
                                   "A maioria dos endpoints requer autenticação via JWT (JSON Web Token). \n" +
                                   "Para autenticar, use o endpoint `/api/auth/login` para obter um token de acesso.\n" +
                                   "Adicione o token no cabeçalho das requisições subsequentes:\n" +
                                   "`Authorization: Bearer <seu_token_aqui>`\n\n" +
                                   "## Perfis de Usuário\n" +
                                   "- **Administrador (tipo 1)**: Acesso completo a todos os endpoints\n" +
                                   "- **Advogado (tipo 2)**: Acesso a endpoints de processos e correspondentes\n" +
                                   "- **Correspondente (tipo 3)**: Acesso limitado a endpoints específicos\n\n" +
                                   "## Formatos de Dados\n" +
                                   "- **Datas**: Formato ISO 8601 (ex: 2025-09-06T18:00:00)\n" +
                                   "- **Tipos de Usuário**: 1 (Administrador), 2 (Advogado), 3 (Correspondente)")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@cra.adv.br")
                                .url("https://cra.adv.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("Token de acesso JWT")));
    }
}