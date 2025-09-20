package br.adv.cra.controller;

import br.adv.cra.service.GoogleDriveService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/google-drive")
@RequiredArgsConstructor
@Tag(name = "google-drive", description = "Operações relacionadas à integração com Google Drive")
public class GoogleDriveController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveController.class);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    
    private final GoogleDriveService googleDriveService;
    
    @Value("${google.drive.oauth.client.id}")
    private String clientId;
    
    @Value("${google.drive.oauth.client.secret}")
    private String clientSecret;
    
    @Value("${google.drive.oauth.redirect.uri}")
    private String redirectUri;
    
    /**
     * Endpoint to get the Google Drive authorization URL for testing
     * 
     * @return ResponseEntity with the authorization URL
     */
    @GetMapping("/authorize")
    @Operation(
        summary = "Obter URL de autorização do Google Drive",
        description = "Obtém a URL de autorização OAuth2 para conectar a conta do Google Drive"
    )
    @ApiResponse(responseCode = "200", description = "URL de autorização retornada com sucesso",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl() {
        try {
            // Build the Google Client Secrets
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            web.setRedirectUris(Collections.singletonList(redirectUri));
            
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setWeb(web);
            
            // Build the authorization flow
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    Collections.singletonList(DriveScopes.DRIVE_FILE))
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();
            
            // Create the authorization URL
            String authorizationUrl = flow.newAuthorizationUrl()
                    .setRedirectUri(redirectUri)
                    .build();
            
            logger.info("Generated Google OAuth2 authorization URL: {}", authorizationUrl);
            
            // Return the authorization URL
            Map<String, String> response = new HashMap<>();
            response.put("authorizationUrl", authorizationUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error generating Google Drive authorization URL: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to generate Google Drive authorization URL: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Simple endpoint to get just the authorization URL as plain text
     * 
     * @return ResponseEntity with the authorization URL as plain text
     */
    @GetMapping("/auth-url")
    @Operation(
        summary = "Obter URL de autorização do Google Drive (apenas URL)",
        description = "Obtém apenas a URL de autorização OAuth2 para conectar a conta do Google Drive"
    )
    @ApiResponse(responseCode = "200", description = "URL de autorização retornada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<String> getAuthorizationUrlSimple() {
        try {
            // Build the Google Client Secrets
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            web.setRedirectUris(Collections.singletonList(redirectUri));
            
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setWeb(web);
            
            // Build the authorization flow
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    Collections.singletonList(DriveScopes.DRIVE_FILE))
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();
            
            // Create the authorization URL
            String authorizationUrl = flow.newAuthorizationUrl()
                    .setRedirectUri(redirectUri)
                    .build();
            
            logger.info("Generated Google OAuth2 authorization URL: {}", authorizationUrl);
            
            // Return just the URL as plain text
            return ResponseEntity.ok(authorizationUrl);
        } catch (Exception e) {
            logger.error("Error generating Google Drive authorization URL: ", e);
            return ResponseEntity.status(500).body("Failed to generate authorization URL: " + e.getMessage());
        }
    }
    
    /**
     * Simple endpoint to test if Google Drive service is working
     * 
     * @return ResponseEntity with connection status
     */
    @GetMapping("/status")
    @Operation(
        summary = "Verificar status da conexão com o Google Drive",
        description = "Verifica se o serviço do Google Drive está funcionando"
    )
    @ApiResponse(responseCode = "200", description = "Status retornado com sucesso",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> getConnectionStatus() {
        Map<String, Object> response = new HashMap<>();
        
        // Actually test Google Drive connectivity and authentication
        boolean isAvailable = googleDriveService.isGoogleDriveAvailable();
        
        if (isAvailable) {
            response.put("message", "Google Drive service is available and properly authenticated");
            response.put("status", "OK");
        } else {
            response.put("message", "Google Drive service is not available or not properly authenticated");
            response.put("status", "UNAVAILABLE");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to check the token status for Google Drive
     * 
     * @return ResponseEntity with token status
     */
    @GetMapping("/token-status")
    @Operation(
        summary = "Verificar status dos tokens do Google Drive",
        description = "Verifica se os tokens do Google Drive estão configurados e válidos"
    )
    @ApiResponse(responseCode = "200", description = "Status dos tokens retornado com sucesso",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> getTokenStatus() {
        Map<String, Object> response = new HashMap<>();
        
        // Check if tokens are set
        boolean hasTokens = googleDriveService.getAccessToken() != null && 
                           !googleDriveService.getAccessToken().isEmpty();
        
        // Check if tokens are valid
        boolean hasValidTokens = googleDriveService.hasValidTokens();
        
        response.put("hasTokens", hasTokens);
        response.put("hasValidTokens", hasValidTokens);
        response.put("accessToken", hasTokens ? "SET" : "NOT_SET");
        response.put("refreshToken", googleDriveService.getRefreshToken() != null && 
                                   !googleDriveService.getRefreshToken().isEmpty() ? "SET" : "NOT_SET");
        
        if (hasTokens) {
            response.put("message", "Google Drive tokens are configured");
            response.put("status", hasValidTokens ? "VALID" : "EXPIRED");
        } else {
            response.put("message", "Google Drive tokens are not configured");
            response.put("status", "MISSING");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Callback endpoint for Google Drive OAuth2 flow
     * 
     * @param code Authorization code from Google
     * @param state State parameter (usually user ID)
     * @return ResponseEntity with success or error message
     */
    @GetMapping("/callback")
    @Operation(
        summary = "Callback para autenticação OAuth2 do Google Drive",
        description = "Endpoint chamado pelo Google após autorização do usuário"
    )
    @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    public ResponseEntity<Map<String, Object>> handleOAuth2Callback(
            @Parameter(description = "Código de autorização do Google") @RequestParam(required = false) String code,
            @Parameter(description = "Parâmetro de estado") @RequestParam(required = false) String state) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Log all request parameters for debugging
        logger.info("Received OAuth2 callback with code: {} and state: {}", code, state);
        
        if (code == null || code.isEmpty()) {
            logger.warn("Authorization code is missing in callback");
            response.put("error", "Authorization code is missing");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Build the Google Client Secrets
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            web.setRedirectUris(Collections.singletonList(redirectUri));
            
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setWeb(web);
            
            // Log the redirect URI being used
            logger.info("Using redirect URI for token exchange: {}", redirectUri);
            
            // Build the authorization flow
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    Collections.singletonList(DriveScopes.DRIVE_FILE))
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();
            
            // Exchange the authorization code for tokens
            logger.info("Exchanging authorization code for tokens");
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();
            
            // Process the token response and store tokens
            logger.info("Successfully exchanged authorization code for tokens");
            logger.info("Access token: {}", tokenResponse.getAccessToken() != null ? "RECEIVED" : "NULL");
            logger.info("Refresh token: {}", tokenResponse.getRefreshToken() != null ? "RECEIVED" : "NULL");
            logger.info("Token expires in: {} seconds", tokenResponse.getExpiresInSeconds());
            
            // Pass tokens to GoogleDriveService for storage/use
            googleDriveService.setTokens(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
            
            response.put("message", "Google Drive authorization successful");
            response.put("access_token_received", tokenResponse.getAccessToken() != null);
            response.put("refresh_token_received", tokenResponse.getRefreshToken() != null);
            if (state != null) {
                response.put("state", state);
            }
            
            return ResponseEntity.ok(response);
        } catch (com.google.api.client.auth.oauth2.TokenResponseException e) {
            logger.error("Token response error during OAuth2 callback processing: ", e);
            logger.error("Error details - Status code: {}, Details: {}", e.getStatusCode(), e.getDetails());
            
            response.put("error", "Token exchange failed: " + e.getMessage());
            
            // Handle the IOException that can be thrown by toPrettyString()
            try {
                response.put("error_details", e.getDetails() != null ? e.getDetails().toPrettyString() : "No details available");
            } catch (IOException ioException) {
                logger.warn("Failed to serialize error details: ", ioException);
                response.put("error_details", "Failed to serialize error details: " + ioException.getMessage());
            }
            
            response.put("status_code", e.getStatusCode());
            
            return ResponseEntity.status(e.getStatusCode() >= 400 ? e.getStatusCode() : 500).body(response);
        } catch (Exception e) {
            logger.error("Error processing OAuth2 callback: ", e);
            response.put("error", "Failed to process callback: " + e.getMessage());
            response.put("exception_type", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}