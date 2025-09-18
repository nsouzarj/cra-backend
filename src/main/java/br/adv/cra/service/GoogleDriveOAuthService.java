package br.adv.cra.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
public class GoogleDriveOAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveOAuthService.class);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String APPLICATION_NAME = "CRA Backend";
    
    @Value("${google.drive.oauth.client.id:}")
    private String clientId;
    
    @Value("${google.drive.oauth.client.secret:}")
    private String clientSecret;
    
    @Value("${google.drive.oauth.redirect.uri:http://localhost:8081/callback}")
    private String redirectUri;
    
    @Value("${google.drive.oauth.enabled:false}")
    private boolean oauthEnabled;
    
    @Value("${google.drive.folder.id:}")
    private String folderId;
    
    private NetHttpTransport HTTP_TRANSPORT;
    private Drive driveService;
    private boolean initialized = false;

    @PostConstruct
    public void init() {
        if (!oauthEnabled) {
            logger.info("Google Drive OAuth is disabled");
            return;
        }
        
        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            logger.warn("Google Drive OAuth is enabled but client ID or secret is not configured");
            return;
        }
        
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            // For server-to-server OAuth, we'll use service account approach
            // But for user OAuth, we need to implement the flow properly
            initialized = true;
            logger.info("Google Drive OAuth service initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Google Drive OAuth service: {}", e.getMessage(), e);
            initialized = false;
        }
    }

    /**
     * Create a Google Drive service instance using client credentials
     */
    private Drive createDriveService() throws IOException, GeneralSecurityException {
        // Create client secrets from the configured values
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setInstalled(details);

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();
        
        // For server applications, we might want to use a simpler approach
        // Let's create a credential with the client ID and secret directly
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build();
        
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Upload a file to Google Drive
     */
    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        if (!initialized) {
            throw new IllegalStateException("Google Drive OAuth service is not initialized");
        }
        
        try {
            // Lazy initialize the drive service
            if (driveService == null) {
                driveService = createDriveService();
            }
            
            // Create file metadata
            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            
            // If a folder ID is specified, set the parent folder
            if (folderId != null && !folderId.isEmpty()) {
                fileMetadata.setParents(Collections.singletonList(folderId));
            }
            
            // Create InputStreamContent
            InputStreamContent mediaContent = new InputStreamContent(
                    file.getContentType(),
                    new ByteArrayInputStream(file.getBytes())
            );
            
            // Upload the file
            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            
            logger.info("File uploaded to Google Drive with ID: {}", uploadedFile.getId());
            return uploadedFile.getId();
        } catch (GeneralSecurityException e) {
            logger.error("Security exception during Google Drive upload: {}", e.getMessage(), e);
            throw new IOException("Failed to upload file to Google Drive", e);
        }
    }

    /**
     * Download a file from Google Drive
     */
    public InputStream downloadFile(String fileId) throws IOException {
        if (!initialized) {
            throw new IllegalStateException("Google Drive OAuth service is not initialized");
        }
        
        try {
            // Lazy initialize the drive service
            if (driveService == null) {
                driveService = createDriveService();
            }
            
            return driveService.files().get(fileId).executeMediaAsInputStream();
        } catch (GeneralSecurityException e) {
            logger.error("Security exception during Google Drive download: {}", e.getMessage(), e);
            throw new IOException("Failed to download file from Google Drive", e);
        }
    }

    /**
     * Delete a file from Google Drive
     */
    public void deleteFile(String fileId) throws IOException {
        if (!initialized) {
            throw new IllegalStateException("Google Drive OAuth service is not initialized");
        }
        
        try {
            // Lazy initialize the drive service
            if (driveService == null) {
                driveService = createDriveService();
            }
            
            driveService.files().delete(fileId).execute();
            logger.info("File deleted from Google Drive with ID: {}", fileId);
        } catch (GeneralSecurityException e) {
            logger.error("Security exception during Google Drive delete: {}", e.getMessage(), e);
            throw new IOException("Failed to delete file from Google Drive", e);
        }
    }

    /**
     * List files in Google Drive
     */
    public List<File> listFiles() throws IOException {
        if (!initialized) {
            throw new IllegalStateException("Google Drive OAuth service is not initialized");
        }
        
        try {
            // Lazy initialize the drive service
            if (driveService == null) {
                driveService = createDriveService();
            }
            
            FileList result = driveService.files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            
            return result.getFiles();
        } catch (GeneralSecurityException e) {
            logger.error("Security exception during Google Drive list: {}", e.getMessage(), e);
            throw new IOException("Failed to list files from Google Drive", e);
        }
    }

    /**
     * Check if the service is properly initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
}