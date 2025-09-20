# Simplified Google Drive Integration

## Overview
This document explains the simplified Google Drive integration implementation that can use either service account credentials or application OAuth2 credentials to upload files directly to a specified folder in Google Drive.

## Key Features
- Supports both service account and application OAuth2 authentication
- Stores all files in a predefined Google Drive folder
- No user-specific authentication required
- Simplified API with no user ID parameter needed

## Configuration

### Application Properties
Configure the following properties in your `application.properties`:

#### Option 1: Service Account (Recommended for Production)
```properties
# Enable Google Drive integration
google.drive.oauth.enabled=true

# Service Account Key Path (enables service account mode)
google.service.account.key.path=/path/to/service-account-key.json

# Google Drive folder ID (optional, if you want to store files in a specific folder)
google.drive.folder.id=your-folder-id
```

#### Option 2: Application OAuth2 (For Development/Testing)
```properties
# Enable Google Drive integration
google.drive.oauth.enabled=true

# Google Drive OAuth client credentials
google.drive.oauth.client.id=your-client-id
google.drive.oauth.client.secret=your-client-secret

# Google Drive folder ID (optional, if you want to store files in a specific folder)
google.drive.folder.id=your-folder-id
```

### Environment Variables
For security, it's recommended to use environment variables for sensitive credentials:

```bash
# Windows (Command Prompt) - Service Account
set GOOGLE_SERVICE_ACCOUNT_KEY_PATH=C:\path\to\service-account-key.json
set GOOGLE_DRIVE_FOLDER_ID=your-folder-id

# Windows (PowerShell) - Service Account
$env:GOOGLE_SERVICE_ACCOUNT_KEY_PATH="C:\path\to\service-account-key.json"
$env:GOOGLE_DRIVE_FOLDER_ID="your-folder-id"

# Windows (Command Prompt) - Application OAuth2
set GOOGLE_DRIVE_CLIENT_ID=your-client-id
set GOOGLE_DRIVE_CLIENT_SECRET=your-client-secret
set GOOGLE_DRIVE_FOLDER_ID=your-folder-id

# Windows (PowerShell) - Application OAuth2
$env:GOOGLE_DRIVE_CLIENT_ID="your-client-id"
$env:GOOGLE_DRIVE_CLIENT_SECRET="your-client-secret"
$env:GOOGLE_DRIVE_FOLDER_ID="your-folder-id"
```

## API Usage

### Upload File
To upload a file to Google Drive, use the SoliArquivo API with `storageLocation=google_drive`:

```http
POST /api/soli-arquivos/upload
Content-Type: multipart/form-data

file: [file data]
solicitacaoId: 123
origem: usuario
storageLocation: google_drive
```

### Download File
To download a file from Google Drive:

```http
GET /api/soli-arquivos/{id}/download
```

### Delete File
To delete a file from Google Drive:

```http
DELETE /api/soli-arquivos/{id}?origem=usuario
```

## Implementation Details

### GoogleDriveService
The `GoogleDriveService` supports both authentication methods:

```java
public class GoogleDriveService {
    @Value("${google.drive.oauth.client.id}")
    private String clientId;
    
    @Value("${google.drive.oauth.client.secret}")
    private String clientSecret;
    
    @Value("${google.drive.folder.id:}")
    private String folderId;
    
    @Value("${google.service.account.key.path:}")
    private String serviceAccountKeyPath;
    
    public String uploadFile(MultipartFile file) throws IOException {
        // Implementation supports both service account and OAuth2
    }
}
```

### SoliArquivoService
The `SoliArquivoService` handles the storage location selection:

```java
public SoliArquivo salvarAnexo(MultipartFile file, Long solicitacaoId, String origem, String storageLocation) throws IOException {
    if (googleDriveEnabled && "google_drive".equals(storageLocation)) {
        return saveFileToGoogleDrive(file, solicitacao, origem);
    } else {
        return saveFileLocally(file, solicitacao, origem);
    }
}
```

## Setup Instructions

### Option 1: Service Account Setup (Recommended)

#### 1. Google Cloud Console Configuration
1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Drive API for your project
4. Create a Service Account:
   - Go to "APIs & Services" > "Credentials"
   - Click "Create Credentials" > "Service account"
   - Fill in the service account details
   - Grant necessary permissions (Drive API access)
5. Create and download a key for the service account (JSON format)

#### 2. Google Drive Folder Setup
1. Create a folder in Google Drive for file storage
2. Note the folder ID from the URL (the long string after `/folders/`)
3. Share the folder with the service account email (found in the JSON key file)

#### 3. Application Configuration
1. Place the service account JSON key file in a secure location
2. Set the `google.service.account.key.path` property to the path of the JSON file
3. Set the `google.drive.folder.id` property to your folder ID
4. Test the integration with a sample file upload

### Option 2: Application OAuth2 Setup (Development Only)

#### 1. Google Cloud Console Configuration
1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Drive API
4. Create OAuth2 credentials:
   - Go to "APIs & Services" > "Credentials"
   - Click "Create Credentials" > "OAuth client ID"
   - Select "Desktop application" as the application type
   - Note the Client ID and Client Secret

#### 2. Manual Token Setup
For application OAuth2 to work, you need to obtain and configure access tokens manually:
1. Implement the OAuth2 flow to get access tokens
2. Store the access tokens securely
3. Configure the GoogleCredential with the access tokens

**Note**: This approach is complex and not recommended for production use. Use service accounts instead.

## Security Considerations

### Credential Management
- Never commit credentials to version control
- Use environment variables for sensitive data
- Rotate credentials regularly
- Use the least privileged account necessary

### Service Account Security
- Store the service account JSON key file securely
- Limit the permissions granted to the service account
- Monitor usage of the service account

### Access Control
- All files are stored using the same credentials
- Access to files depends on the Google Drive folder permissions
- Ensure the folder has appropriate sharing settings

## Troubleshooting

### Common Issues
1. **Authentication errors**: 
   - For service accounts: Verify the JSON key file path and permissions
   - For OAuth2: Ensure valid access tokens are configured
2. **Permission denied**: Check folder sharing settings
3. **Timeout errors**: Check network connectivity to Google services
4. **File not found**: Verify folder ID is correct
5. **SocketException errors**: See specific troubleshooting guide below

### SocketException Troubleshooting
If you encounter `java.net.SocketException: An established connection was aborted by the software in your host machine` errors:

1. **Check network connectivity**:
   - Ensure stable internet connection
   - Test connectivity to `www.googleapis.com`

2. **Verify firewall settings**:
   - Allow outbound connections to Google services
   - Check antivirus software interference

3. **Check proxy configuration**:
   - Verify no incorrect proxy is configured
   - Add Google services to proxy bypass list if needed

4. **Increase timeout values**:
   - The service uses 30-second timeouts by default
   - Consider increasing if you have slow network connectivity

See [GOOGLE_DRIVE_SOCKET_EXCEPTION_TROUBLESHOOTING.md](GOOGLE_DRIVE_SOCKET_EXCEPTION_TROUBLESHOOTING.md) for detailed troubleshooting steps.

### Logging
Enable debug logging to troubleshoot issues:

```properties
logging.level.br.adv.cra.service.GoogleDriveService=DEBUG
```

## Limitations
- All files are stored using the same credentials
- No user-specific file access control
- OAuth2 approach requires manual token management
- Limited to the permissions granted to the credentials

## Error Handling and Fallbacks
The implementation includes robust error handling:
- Network errors (SocketException) trigger fallback to local storage
- Timeout handling prevents the application from hanging
- Detailed logging helps with troubleshooting
- Graceful degradation when Google Drive is unavailable

## Recommendation
For production environments, use the **Service Account** approach as it:
- Is simpler to configure and maintain
- Doesn't require manual token management
- Provides better security through key-based authentication
- Is more reliable for server-to-server communication