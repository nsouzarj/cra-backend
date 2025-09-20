package br.adv.cra.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class GoogleDriveService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveService.class);
    private static final String APPLICATION_NAME = "CRA Backend";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final int MAX_RETRIES = 3;
    private static final int CONNECT_TIMEOUT_MS = 30000; // 30 seconds
    private static final int READ_TIMEOUT_MS = 30000;    // 30 seconds
    
    @Value("${google.drive.oauth.client.id}")
    private String clientId;
    
    @Value("${google.drive.oauth.client.secret}")
    private String clientSecret;
    
    @Value("${google.drive.folder.id:}")
    private String folderId;
    
    // OAuth2 tokens
    private volatile String accessToken;
    private volatile String refreshToken;
    private volatile long tokenExpirationTime = 0; // Unix timestamp when token expires
    
    /**
     * Set OAuth2 tokens for Google Drive API access
     * 
     * @param accessToken The access token
     * @param refreshToken The refresh token (optional)
     */
    public void setTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        // Set expiration time to 50 minutes from now (tokens usually expire in 1 hour)
        this.tokenExpirationTime = System.currentTimeMillis() + (50 * 60 * 1000);
        logger.info("Google Drive OAuth2 tokens updated - Access token: {}, Refresh token: {}, Expires at: {}", 
            accessToken != null ? "SET" : "NULL", refreshToken != null ? "SET" : "NULL", 
            new java.util.Date(tokenExpirationTime));
    }
    
    /**
     * Get the current access token
     * 
     * @return The current access token or null if not set
     */
    public String getAccessToken() {
        return accessToken;
    }
    
    /**
     * Get the current refresh token
     * 
     * @return The current refresh token or null if not set
     */
    public String getRefreshToken() {
        return refreshToken;
    }
    
    /**
     * Check if we have valid tokens
     * 
     * @return true if we have valid tokens, false otherwise
     */
    public boolean hasValidTokens() {
        boolean hasTokens = accessToken != null && !accessToken.isEmpty() && 
               System.currentTimeMillis() < tokenExpirationTime;
        
        logger.debug("Token validation - Access token: {}, Not expired: {}, Valid: {}", 
            accessToken != null ? "SET" : "NULL", 
            System.currentTimeMillis() < tokenExpirationTime,
            hasTokens);
        
        return hasTokens;
    }
    
    /**
     * Refresh the access token using the refresh token
     * 
     * @throws IOException if token refresh fails
     */
    private void refreshAccessToken() throws IOException {
        if (refreshToken == null || refreshToken.isEmpty()) {
            logger.warn("No refresh token available to refresh access token");
            throw new IOException("No refresh token available. Please re-authenticate with Google Drive.");
        }
        
        try {
            logger.info("Refreshing access token using refresh token");
            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            
            // Create refresh token request
            GoogleTokenResponse tokenResponse = new GoogleRefreshTokenRequest(
                transport, JSON_FACTORY,
                refreshToken,
                clientId, clientSecret)
                .execute();
            
            // Update tokens
            this.accessToken = tokenResponse.getAccessToken();
            // Refresh tokens are long-lived, but we might get a new one
            if (tokenResponse.getRefreshToken() != null && 
                !tokenResponse.getRefreshToken().isEmpty()) {
                this.refreshToken = tokenResponse.getRefreshToken();
            }
            
            // Update expiration time
            this.tokenExpirationTime = System.currentTimeMillis() + 
                (tokenResponse.getExpiresInSeconds() != null ? 
                 tokenResponse.getExpiresInSeconds() * 1000 : 50 * 60 * 1000);
            
            logger.info("Access token refreshed successfully. New expiration: {}", 
                new java.util.Date(tokenExpirationTime));
        } catch (com.google.api.client.auth.oauth2.TokenResponseException e) {
            logger.error("Token response error during refresh: ", e);
            if (e.getDetails() != null) {
                logger.error("Error details: {}", e.getDetails().toPrettyString());
            }
            
            // If it's a 400 error, the refresh token is likely invalid
            if (e.getStatusCode() == 400) {
                logger.error("Refresh token is invalid or expired. User needs to re-authenticate.");
                clearTokens(); // Clear invalid tokens
                throw new IOException("Refresh token is invalid. Please re-authenticate with Google Drive.", e);
            }
            
            throw new IOException("Failed to refresh access token: " + e.getMessage(), e);
        } catch (GeneralSecurityException e) {
            logger.error("Security exception during token refresh: ", e);
            throw new IOException("Failed to refresh access token due to security issues", e);
        } catch (Exception e) {
            logger.error("Error refreshing access token: ", e);
            throw new IOException("Failed to refresh access token: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ensure we have a valid access token, refreshing if necessary
     * 
     * @throws IOException if token refresh fails
     */
    private void ensureValidAccessToken() throws IOException {
        if (!hasValidTokens()) {
            if (refreshToken != null && !refreshToken.isEmpty()) {
                logger.info("Access token expired or missing, attempting to refresh");
                refreshAccessToken();
            } else {
                logger.warn("No valid tokens available and no refresh token to refresh with");
                throw new IOException("No valid tokens available. Please re-authenticate with Google Drive.");
            }
        } else {
            logger.debug("Access token is valid and not expired");
        }
    }
    
    /**
     * Generate a unique filename using UUID + original filename
     * 
     * @param originalFilename The original filename
     * @return The unique filename
     */
    private String generateUniqueFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        
        String fileExtension = "";
        String fileNameWithoutExtension = originalFilename;
        
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            fileExtension = originalFilename.substring(lastDotIndex);
            fileNameWithoutExtension = originalFilename.substring(0, lastDotIndex);
        }
        
        // Create filename as UUID + original filename (without extension) + extension
        return UUID.randomUUID().toString() + "_" + fileNameWithoutExtension + fileExtension;
    }
    
    /**
     * Upload a file to Google Drive
     * 
     * @param file The file to upload
     * @return The Google Drive file ID
     * @throws IOException If there's an error during the upload
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Ensure we have valid tokens before proceeding
        ensureValidAccessToken();
        
        IOException lastException = null;
        
        // Retry mechanism for network issues
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.info("Starting Google Drive file upload for file: {}, attempt {}/{}", 
                    file.getOriginalFilename(), attempt, MAX_RETRIES);
                
                // Create Google Drive service
                Drive service = createDriveService();
                
                // Generate unique filename to prevent overwrites
                String uniqueFilename = generateUniqueFilename(file.getOriginalFilename());
                logger.info("Generated unique filename: {}", uniqueFilename);
                
                // Create file metadata with unique filename
                File fileMetadata = new File();
                fileMetadata.setName(uniqueFilename);
                
                // Set parent folder if configured
                if (folderId != null && !folderId.isEmpty()) {
                    fileMetadata.setParents(Collections.singletonList(folderId));
                    logger.info("Setting parent folder ID: {}", folderId);
                }
                
                // Create input stream content
                logger.info("Creating input stream content for file upload");
                InputStreamContent mediaContent = new InputStreamContent(
                        file.getContentType(),
                        file.getInputStream()
                );
                
                // Upload file
                logger.info("Uploading file to Google Drive");
                File uploadedFile = service.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
                
                logger.info("File uploaded successfully to Google Drive with ID: {}", uploadedFile.getId());
                return uploadedFile.getId();
            } catch (GeneralSecurityException e) {
                logger.error("Security exception during Google Drive upload: ", e);
                throw new IOException("Failed to upload file to Google Drive due to security issues", e);
            } catch (IOException e) {
                lastException = e;
                logger.warn("Attempt {}/{} failed with IOException: {}", attempt, MAX_RETRIES, e.getMessage());
                
                // Log detailed error information
                logDetailedErrorInfo(e);
                
                // Don't retry on certain types of errors
                if (isNonRetryableError(e)) {
                    logger.error("Non-retryable error occurred during upload", e);
                    throw e;
                }
                
                // Wait before retrying (exponential backoff)
                if (attempt < MAX_RETRIES) {
                    try {
                        long waitTime = (long) Math.pow(2, attempt) * 1000; // Exponential backoff
                        logger.info("Waiting {} ms before retrying...", waitTime);
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Upload interrupted", ie);
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected error during Google Drive upload: ", e);
                throw new IOException("Failed to upload file to Google Drive: " + e.getMessage(), e);
            }
        }
        
        // If we get here, all retries failed
        logger.error("All {} attempts to upload file to Google Drive failed", MAX_RETRIES);
        throw new IOException("Failed to upload file to Google Drive after " + MAX_RETRIES + " attempts", lastException);
    }
    
    /**
     * Download a file from Google Drive
     * 
     * @param fileId The Google Drive file ID
     * @return InputStream of the file content
     * @throws IOException If there's an error during the download
     */
    public InputStream downloadFile(String fileId) throws IOException {
        // Ensure we have valid tokens before proceeding
        ensureValidAccessToken();
        
        IOException lastException = null;
        
        // Retry mechanism for network issues
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.info("Starting Google Drive file download for file ID: {}, attempt {}/{}", 
                    fileId, attempt, MAX_RETRIES);
                
                // Create Google Drive service
                Drive service = createDriveService();
                
                // Download file
                logger.info("Downloading file from Google Drive");
                InputStream result = service.files().get(fileId).executeMediaAsInputStream();
                logger.info("File downloaded successfully from Google Drive");
                
                return result;
            } catch (GeneralSecurityException e) {
                logger.error("Security exception during Google Drive download: ", e);
                throw new IOException("Failed to download file from Google Drive due to security issues", e);
            } catch (IOException e) {
                lastException = e;
                logger.warn("Attempt {}/{} failed with IOException: {}", attempt, MAX_RETRIES, e.getMessage());
                
                // Log detailed error information
                logDetailedErrorInfo(e);
                
                // Don't retry on certain types of errors
                if (isNonRetryableError(e)) {
                    logger.error("Non-retryable error occurred during download", e);
                    throw e;
                }
                
                // Wait before retrying (exponential backoff)
                if (attempt < MAX_RETRIES) {
                    try {
                        long waitTime = (long) Math.pow(2, attempt) * 1000; // Exponential backoff
                        logger.info("Waiting {} ms before retrying...", waitTime);
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Download interrupted", ie);
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected error during Google Drive download: ", e);
                throw new IOException("Failed to download file from Google Drive: " + e.getMessage(), e);
            }
        }
        
        // If we get here, all retries failed
        logger.error("All {} attempts to download file from Google Drive failed", MAX_RETRIES);
        throw new IOException("Failed to download file from Google Drive after " + MAX_RETRIES + " attempts", lastException);
    }
    
    /**
     * Delete a file from Google Drive
     * 
     * @param fileId The Google Drive file ID
     * @throws IOException If there's an error during the deletion
     */
    public void deleteFile(String fileId) throws IOException {
        // Ensure we have valid tokens before proceeding
        ensureValidAccessToken();
        
        IOException lastException = null;
        
        // Retry mechanism for network issues
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.info("Starting Google Drive file deletion for file ID: {}, attempt {}/{}", 
                    fileId, attempt, MAX_RETRIES);
                
                // Create Google Drive service
                Drive service = createDriveService();
                
                // Delete file
                logger.info("Deleting file from Google Drive");
                service.files().delete(fileId).execute();
                logger.info("File deleted successfully from Google Drive with ID: {}", fileId);
                return;
            } catch (GeneralSecurityException e) {
                logger.error("Security exception during Google Drive deletion: ", e);
                throw new IOException("Failed to delete file from Google Drive due to security issues", e);
            } catch (IOException e) {
                lastException = e;
                logger.warn("Attempt {}/{} failed with IOException: {}", attempt, MAX_RETRIES, e.getMessage());
                
                // Log detailed error information
                logDetailedErrorInfo(e);
                
                // Don't retry on certain types of errors
                if (isNonRetryableError(e)) {
                    logger.error("Non-retryable error occurred during deletion", e);
                    throw e;
                }
                
                // Wait before retrying (exponential backoff)
                if (attempt < MAX_RETRIES) {
                    try {
                        long waitTime = (long) Math.pow(2, attempt) * 1000; // Exponential backoff
                        logger.info("Waiting {} ms before retrying...", waitTime);
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Deletion interrupted", ie);
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected error during Google Drive deletion: ", e);
                throw new IOException("Failed to delete file from Google Drive: " + e.getMessage(), e);
            }
        }
        
        // If we get here, all retries failed
        logger.error("All {} attempts to delete file from Google Drive failed", MAX_RETRIES);
        throw new IOException("Failed to delete file from Google Drive after " + MAX_RETRIES + " attempts", lastException);
    }
    
    private Drive createDriveService() throws GeneralSecurityException, IOException {
        logger.info("Creating Google Drive service instance");
        
        // Use ExecutorService to implement timeout for HTTP transport creation
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Drive> future = executor.submit(() -> {
            try {
                logger.info("Creating HTTP transport for Google Drive service");
                HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
                logger.info("HTTP transport created successfully");
                
                // Create credential with OAuth2 tokens if available, otherwise use application credentials
                GoogleCredential credential;
                
                if (accessToken != null && !accessToken.isEmpty()) {
                    // Use OAuth2 tokens - create credential with all required components
                    credential = new GoogleCredential.Builder()
                        .setTransport(transport)
                        .setJsonFactory(JSON_FACTORY)
                        .setClientSecrets(clientId, clientSecret)
                        .build();
                    credential.setAccessToken(accessToken);
                    if (refreshToken != null && !refreshToken.isEmpty()) {
                        credential.setRefreshToken(refreshToken);
                    }
                    logger.info("Using OAuth2 tokens for Google Drive service - Access token: {}, Refresh token: {}", 
                        accessToken != null ? "SET" : "NULL", refreshToken != null ? "SET" : "NULL");
                } else {
                    // Fall back to application credentials
                    credential = new GoogleCredential.Builder()
                        .setTransport(transport)
                        .setJsonFactory(JSON_FACTORY)
                        .setClientSecrets(clientId, clientSecret)
                        .build();
                    logger.info("Using application credentials for Google Drive service");
                }
                
                // Create HTTP request initializer with timeout settings
                HttpRequestInitializer requestInitializer = new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        credential.initialize(request);
                        request.setConnectTimeout(CONNECT_TIMEOUT_MS);
                        request.setReadTimeout(READ_TIMEOUT_MS);
                        request.setNumberOfRetries(3);
                        logger.debug("HTTP request initialized with timeouts: connect={}, read={}", 
                            CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
                    }
                };
                
                // Build the Drive service with timeout settings
                logger.info("Building Drive service with application name: {}", APPLICATION_NAME);
                Drive driveService = new Drive.Builder(transport, JSON_FACTORY, requestInitializer)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
                logger.info("Drive service built successfully");
                
                return driveService;
            } catch (Exception e) {
                logger.error("Error creating Drive service: ", e);
                throw new RuntimeException(e);
            }
        });
        
        try {
            // Wait for the service creation with a timeout
            logger.info("Waiting for Drive service creation (timeout: 30 seconds)");
            return future.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error during Drive service creation: ", e);
            throw new IOException("Failed to create Drive service", e);
        } catch (TimeoutException e) {
            logger.error("Timeout while creating Drive service (30 seconds elapsed)");
            future.cancel(true); // Cancel the task
            throw new IOException("Timeout while creating Drive service. This might indicate network connectivity issues.", e);
        } finally {
            executor.shutdown();
        }
    }
    
    /**
     * Check if an error is non-retryable
     */
    private boolean isNonRetryableError(IOException e) {
        String message = e.getMessage();
        if (message == null) return false;
        
        // Don't retry on authentication errors or bad requests
        return message.contains("401") || 
               message.contains("403") || 
               message.contains("400") || 
               message.contains("404");
    }
    
    /**
     * Log detailed error information for debugging
     */
    private void logDetailedErrorInfo(IOException e) {
        logger.warn("Detailed error information:");
        logger.warn("  Exception class: {}", e.getClass().getName());
        logger.warn("  Exception message: {}", e.getMessage());
        
        // Log stack trace elements related to Google APIs
        for (StackTraceElement element : e.getStackTrace()) {
            if (element.getClassName().contains("google") || 
                element.getClassName().contains("Google") ||
                element.getClassName().contains("apache") ||
                element.getClassName().contains("http")) {
                logger.warn("  Stack trace element: {}", element.toString());
            }
        }
        
        // Log cause if available
        if (e.getCause() != null) {
            logger.warn("  Cause: {} - {}", e.getCause().getClass().getName(), e.getCause().getMessage());
        }
    }
    
    /**
     * Check if Google Drive is currently available by testing connectivity
     * This method now properly checks OAuth2 authentication
     * 
     * @return true if Google Drive is available and properly authenticated, false otherwise
     */
    public boolean isGoogleDriveAvailable() {
        try {
            logger.info("Testing Google Drive connectivity and authentication");
            
            // First check if we have tokens
            if (accessToken == null || accessToken.isEmpty()) {
                logger.warn("No access token available for Google Drive");
                return false;
            }
            
            Drive service = createDriveService();
            
            // Try a simple operation to test connectivity and authentication
            // Using 'about' requires authentication, unlike just creating the service
            com.google.api.services.drive.model.About about = service.about().get()
                .setFields("user,kind")
                .execute();
            
            logger.info("Google Drive connectivity and authentication test successful");
            logger.info("Authenticated user: {}", about.getUser().getEmailAddress());
            return true;
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            // Specific handling for Google API errors
            logger.warn("Google Drive authentication test failed with Google API error: {}", e.getMessage());
            if (e.getStatusCode() == 401) {
                logger.warn("Authentication failed - invalid or missing credentials");
                return false;
            }
            logger.warn("Google Drive connectivity test failed: ", e);
            return false;
        } catch (Exception e) {
            logger.warn("Google Drive connectivity test failed: ", e);
            return false;
        }
    }
    
    /**
     * Clear stored tokens (for logout/disconnect)
     */
    public void clearTokens() {
        this.accessToken = null;
        this.refreshToken = null;
        this.tokenExpirationTime = 0;
        logger.info("Google Drive tokens cleared");
    }
}