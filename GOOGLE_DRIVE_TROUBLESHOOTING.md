# Google Drive Integration Troubleshooting Guide

## Overview
This document provides troubleshooting guidance for common issues with the Google Drive integration in the CRA Backend system, particularly focusing on the hanging issue at `GoogleNetHttpTransport.newTrustedTransport()`.

## Common Issues and Solutions

### 1. Application Hanging at GoogleNetHttpTransport.newTrustedTransport()

#### Problem
The application hangs when trying to initialize the Google Drive service, specifically at the line:
```java
final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
```

#### Root Causes
1. **Network connectivity issues**: The application cannot reach Google's servers
2. **Firewall restrictions**: Network firewall is blocking outbound connections to Google services
3. **DNS resolution problems**: Unable to resolve Google's domain names
4. **Timeout issues**: The transport initialization is taking too long

#### Solutions

##### A. Network Connectivity Check
1. Verify internet connectivity:
   ```bash
   ping www.googleapis.com
   ```

2. Check if you can access Google Drive in a browser

##### B. Firewall Configuration
1. Ensure outbound connections to Google services are allowed:
   - Ports: 80 (HTTP), 443 (HTTPS)
   - Domains: *.googleapis.com, *.google.com

##### C. DNS Resolution
1. Verify DNS resolution:
   ```bash
   nslookup www.googleapis.com
   ```

2. If DNS issues exist, consider:
   - Changing DNS servers (e.g., to Google's 8.8.8.8)
   - Adding entries to hosts file (as a temporary workaround)

##### D. Timeout Configuration
The updated implementation includes timeout handling to prevent indefinite hanging:

```java
private NetHttpTransport createHttpTransportWithTimeout() throws GeneralSecurityException, IOException {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
        Future<NetHttpTransport> future = executor.submit(() -> {
            try {
                return GoogleNetHttpTransport.newTrustedTransport();
            } catch (Exception e) {
                logger.error("Error creating HTTP transport: ", e);
                throw new RuntimeException(e);
            }
        });
        
        // Wait for at most 15 seconds
        return future.get(15, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
        logger.error("Timeout while creating HTTP transport - GoogleNetHttpTransport.newTrustedTransport() is hanging");
        throw new IOException("Timeout while initializing Google Drive service. Please check your network connection and firewall settings.", e);
    } catch (ExecutionException e) {
        logger.error("Error while creating HTTP transport: ", e.getCause());
        throw new IOException("Error while initializing Google Drive service", e.getCause());
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        logger.error("Interrupted while creating HTTP transport: ", e);
        throw new IOException("Interrupted while initializing Google Drive service", e);
    } finally {
        executor.shutdown();
    }
}
```

This implementation:
- Sets a 15-second timeout for transport initialization
- Provides clear error messages when timeouts occur
- Handles interruption and execution exceptions properly

### 2. Service Account Authentication Issues

#### Problem
Files cannot be uploaded or downloaded when using service account authentication.

#### Solutions
1. **Verify service account key file**:
   - Ensure the file exists at the specified path
   - Check file permissions
   - Validate JSON format

2. **Check service account permissions**:
   - Ensure the service account has access to the target Google Drive folder
   - Share the folder with the service account email if needed

3. **Validate scopes**:
   - Ensure the service account is configured with the correct scopes:
     `https://www.googleapis.com/auth/drive.file`

### 3. Per-User OAuth2 Issues

#### Problem
Users cannot connect their Google Drive accounts or files cannot be accessed.

#### Solutions
1. **Check OAuth2 credentials**:
   - Verify Client ID and Client Secret are correct
   - Ensure the redirect URI matches the configured value

2. **Token refresh issues**:
   - If users report access issues, they may need to reconnect their accounts
   - Check if stored tokens are expired or invalid

3. **User credential storage**:
   - Verify the `google_drive_credentials` table contains valid credentials
   - Check if user IDs match between the application and stored credentials

## Logging and Debugging

### Enable Debug Logging
To get more detailed information about Google Drive operations, enable debug logging in your `application.properties`:

```properties
logging.level.br.adv.cra.service.GoogleDriveService=DEBUG
logging.level.com.google.api.client=DEBUG
```

### Key Log Messages
Look for these log messages to diagnose issues:

1. **Transport initialization**:
   ```
   DEBUG Creating GoogleNetHttpTransport...
   DEBUG GoogleNetHttpTransport created successfully
   ```

2. **Service account usage**:
   ```
   INFO Using service account for Google Drive access
   ```

3. **Per-user authentication**:
   ```
   INFO Using user-specific OAuth2 credentials for Google Drive access
   ```

4. **Timeout errors**:
   ```
   ERROR Timeout while creating HTTP transport - GoogleNetHttpTransport.newTrustedTransport() is hanging
   ```

## Testing

### Manual Testing Steps
1. **Verify network connectivity**:
   ```bash
   curl -I https://www.googleapis.com/
   ```

2. **Test Google Drive API access**:
   - Use the Google OAuth2 Playground to test API access
   - Verify your credentials work outside the application

3. **Test with a simple file operation**:
   - Upload a small test file
   - Verify it appears in the target Google Drive folder

### Automated Testing
The system includes unit tests that can help identify issues:

```bash
mvn test -Dtest=GoogleDriveServiceTest
mvn test -Dtest=GoogleDriveServiceTimeoutTest
```

## Environment-Specific Considerations

### Development Environment
- Use localhost URLs for OAuth2 redirects
- Ensure your development machine has internet access
- Consider using a service account for easier testing

### Production Environment
- Verify production firewall rules allow outbound connections to Google services
- Use proper SSL certificates
- Ensure service account keys are securely stored
- Monitor for timeout issues in production logs

## Additional Resources

### Google Cloud Console
- [Google Cloud Console](https://console.cloud.google.com/)
- [Google Drive API Documentation](https://developers.google.com/drive/api)
- [OAuth2 Playground](https://developers.google.com/oauthplayground)

### Common Error Codes
- **401 Unauthorized**: Invalid or expired credentials
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: File or resource not found
- **429 Too Many Requests**: Rate limiting
- **500 Internal Server Error**: Google service error

## Contact Support
If you continue to experience issues:

1. Check the application logs for detailed error messages
2. Verify all configuration values are correct
3. Test network connectivity to Google services
4. Contact system administrators for firewall/DNS issues