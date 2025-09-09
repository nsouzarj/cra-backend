package br.adv.cra.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import br.adv.cra.dto.JwtResponse;
import br.adv.cra.dto.LoginRequest;
import br.adv.cra.dto.RefreshTokenRequest;
import br.adv.cra.dto.RegisterRequest;

import java.util.List;
import java.util.Map;

/**
 * Interface de documentação para a API do CRA.
 * Esta interface contém apenas anotações Swagger para documentar a API
 * sem modificar os controllers existentes.
 */
public interface ApiDocumentation {

    // Auth Controller Documentation
    @Tag(name = "auth", description = "Operações de autenticação e autorização")
    @Operation(
        summary = "Autentica um usuário",
        description = "Autentica um usuário com base nas credenciais fornecidas e retorna tokens JWT. " +
                     "O token de acesso tem validade de 24 horas e o token de atualização tem validade de 7 dias."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida", 
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JwtResponse.class),
                examples = @ExampleObject(value = "{\n" +
                    "  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                    "  \"refreshToken\": \"dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...\",\n" +
                    "  \"type\": \"Bearer\",\n" +
                    "  \"id\": 1,\n" +
                    "  \"login\": \"admin\",\n" +
                    "  \"nomeCompleto\": \"Administrador do Sistema\",\n" +
                    "  \"emailPrincipal\": \"admin@cra.com.br\",\n" +
                    "  \"tipo\": 1,\n" +
                    "  \"roles\": [\"ROLE_ADMIN\"],\n" +
                    "  \"expiresAt\": \"2025-09-07T18:00:00\"\n" +
                    "}"))),
        @ApiResponse(responseCode = "400", description = "Credenciais inválidas",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n" +
                    "  \"error\": \"Credenciais inválidas\",\n" +
                    "  \"message\": \"Bad credentials\"\n" +
                    "}")))
    })
    ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest);

    @Operation(
        summary = "Registra um novo usuário",
        description = "Registra um novo usuário no sistema. Apenas usuários com perfil de administrador podem registrar novos usuários. " +
                     "O tipo de usuário deve ser 1 (Administrador), 2 (Advogado) ou 3 (Correspondente). " +
                     "Quando registrar um usuário do tipo 3 (Correspondente), você pode opcionalmente associar um correspondente existente " +
                     "fornecendo o ID do correspondente no campo correspondenteId."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registro bem-sucedido", 
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JwtResponse.class),
                examples = @ExampleObject(value = "{\n" +
                    "  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                    "  \"refreshToken\": \"dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...\",\n" +
                    "  \"type\": \"Bearer\",\n" +
                    "  \"id\": 2,\n" +
                    "  \"login\": \"novousuario\",\n" +
                    "  \"nomeCompleto\": \"Novo Usuário\",\n" +
                    "  \"emailPrincipal\": \"novo.usuario@cra.com.br\",\n" +
                    "  \"tipo\": 2,\n" +
                    "  \"roles\": [\"ROLE_ADVOGADO\"],\n" +
                    "  \"expiresAt\": \"2025-09-07T18:00:00\"\n" +
                    "}"))),
        @ApiResponse(responseCode = "400", description = "Erro no registro",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n" +
                    "  \"error\": \"Erro no registro\",\n" +
                    "  \"message\": \"Login já existe\"\n" +
                    "}"))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest);

    @Operation(
        summary = "Atualiza o token de acesso",
        description = "Atualiza um token JWT expirado usando o token de atualização. " +
                     "O token de acesso tem validade de 24 horas e o token de atualização tem validade de 7 dias."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token atualizado com sucesso", 
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JwtResponse.class),
                examples = @ExampleObject(value = "{\n" +
                    "  \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n" +
                    "  \"refreshToken\": \"dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...\",\n" +
                    "  \"type\": \"Bearer\",\n" +
                    "  \"id\": 1,\n" +
                    "  \"login\": \"admin\",\n" +
                    "  \"nomeCompleto\": \"Administrador do Sistema\",\n" +
                    "  \"emailPrincipal\": \"admin@cra.com.br\",\n" +
                    "  \"tipo\": 1,\n" +
                    "  \"roles\": [\"ROLE_ADMIN\"],\n" +
                    "  \"expiresAt\": \"2025-09-07T18:00:00\"\n" +
                    "}"))),
        @ApiResponse(responseCode = "400", description = "Token inválido",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n" +
                    "  \"error\": \"Token inválido\",\n" +
                    "  \"message\": \"Refresh token expirado\"\n" +
                    "}")))
    })
    ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request);

    @Operation(
        summary = "Obtém informações do usuário atual",
        description = "Recupera informações detalhadas sobre o usuário autenticado atualmente, " +
                     "incluindo ID, login, nome completo, emails, tipo de usuário e permissões."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Informações do usuário recuperadas com sucesso", 
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n" +
                    "  \"id\": 1,\n" +
                    "  \"login\": \"admin\",\n" +
                    "  \"nomecompleto\": \"Administrador do Sistema\",\n" +
                    "  \"emailPrincipal\": \"admin@cra.com.br\",\n" +
                    "  \"tipo\": 1,\n" +
                    "  \"ativo\": true,\n" +
                    "  \"authorities\": [\"ROLE_ADMIN\"]\n" +
                    "}"))),
        @ApiResponse(responseCode = "400", description = "Usuário não encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n" +
                    "  \"error\": \"Usuário não encontrado\",\n" +
                    "  \"message\": \"Usuário não autenticado\"\n" +
                    "}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<?> getCurrentUser();

    @Operation(
        summary = "Realiza logout do usuário",
        description = "Em um ambiente stateless JWT, o logout é tratado no cliente. " +
                     "Este endpoint apenas confirma a operação e pode ser usado para limpar tokens do lado do servidor se necessário."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso", 
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n" +
                    "  \"message\": \"Logout realizado com sucesso\"\n" +
                    "}")))
    })
    ResponseEntity<?> logoutUser();

    @Operation(
        summary = "Valida o token JWT atual",
        description = "Valida o token JWT atual do usuário autenticado. Retorna informações sobre a validade do token e as permissões do usuário."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token validado com sucesso", 
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n" +
                    "  \"valid\": true,\n" +
                    "  \"user\": \"admin\",\n" +
                    "  \"authorities\": [\"ROLE_ADMIN\"]\n" +
                    "}"))),
        @ApiResponse(responseCode = "400", description = "Token inválido",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\n" +
                    "  \"valid\": false,\n" +
                    "  \"message\": \"Token expirado\"\n" +
                    "}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<?> validateToken();
    
    // User Controller Documentation
    @Tag(name = "usuario", description = "Operações relacionadas a usuários")
    @Operation(
        summary = "Lista todos os usuários",
        description = "Recupera uma lista de todos os usuários cadastrados no sistema. " +
                     "Apenas administradores têm permissão para acessar este endpoint."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuários recuperada com sucesso",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "[\n" +
                    "  {\n" +
                    "    \"id\": 1,\n" +
                    "    \"login\": \"admin\",\n" +
                    "    \"nomecompleto\": \"Administrador do Sistema\",\n" +
                    "    \"emailprincipal\": \"admin@cra.com.br\",\n" +
                    "    \"tipo\": 1,\n" +
                    "    \"ativo\": true\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"id\": 2,\n" +
                    "    \"login\": \"advogado1\",\n" +
                    "    \"nomecompleto\": \"Dr. João Silva\",\n" +
                    "    \"emailprincipal\": \"joao.silva@cra.com.br\",\n" +
                    "    \"tipo\": 2,\n" +
                    "    \"ativo\": true\n" +
                    "  }\n" +
                    "]"))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas administradores")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<List<Map<String, Object>>> getAllUsers();
    
    // Correspondente Controller Documentation
    @Tag(name = "correspondente", description = "Operações relacionadas a correspondentes")
    @Operation(
        summary = "Lista todos os correspondentes",
        description = "Recupera uma lista de todos os correspondentes cadastrados no sistema. " +
                     "Apenas administradores e advogados têm permissão para acessar este endpoint."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de correspondentes recuperada com sucesso",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<List<Map<String, Object>>> getAllCorrespondentes();
    
    // Processo Controller Documentation
    @Tag(name = "processo", description = "Operações relacionadas a processos")
    @Operation(
        summary = "Lista todos os processos",
        description = "Recupera uma lista de todos os processos cadastrados no sistema. " +
                     "Apenas administradores, advogados e correspondentes têm permissão para acessar este endpoint."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de processos recuperada com sucesso",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<List<Map<String, Object>>> getAllProcessos();
    
    // Solicitacao Controller Documentation
    @Tag(name = "solicitacao", description = "Operações relacionadas a solicitações")
    @Operation(
        summary = "Lista todas as solicitações",
        description = "Recupera uma lista de todas as solicitações cadastradas no sistema. " +
                     "Apenas administradores, advogados e correspondentes têm permissão para acessar este endpoint."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de solicitações recuperada com sucesso",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<List<Map<String, Object>>> getAllSolicitacoes();
}