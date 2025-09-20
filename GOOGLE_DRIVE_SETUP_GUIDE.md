# Google Drive Integration Setup Guide

## Overview

This guide explains how to properly configure and set up Google Drive integration for the CRA Backend system. The current implementation includes robust error handling and fallback mechanisms, but requires proper authentication to work correctly.

## Prerequisites

Before setting up Google Drive integration, ensure you have:

1. A Google Cloud Platform account
2. A project created in the Google Cloud Console
3. Billing enabled for your project (required for Google Drive API)

## Step-by-Step Setup

### 1. Enable Google Drive API

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project or create a new one
3. Navigate to "APIs & Services" > "Library"
4. Search for "Google Drive API"
5. Click on it and press "Enable"

### 2. Create OAuth2 Credentials

For the simplified implementation, you'll need to create OAuth2 client credentials:

1. In the Google Cloud Console, go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "OAuth client ID"
3. Select "Desktop application" as the application type
4. Give it a name (e.g., "CRA Backend")
5. Click "Create"
6. Note the Client ID and Client Secret (you'll need these later)

### 3. Configure Application Properties

Update your [application.properties](src/main/resources/application.properties) file with the correct values:

```properties
# Enable Google Drive OAuth integration
google.drive.oauth.enabled=true

# Google Drive OAuth client credentials (from Google Cloud Console)
google.drive.oauth.client.id=YOUR_CLIENT_ID_HERE
google.drive.oauth.client.secret=YOUR_CLIENT_SECRET_HERE

# Google Drive folder ID (optional, if you want to store files in a specific folder)
google.drive.folder.id=YOUR_FOLDER_ID_HERE

# Service Account Key Path (leave empty for OAuth2 client credentials)
google.service.account.key.path=
```

### 4. Optional: Create a Specific Folder for Files

If you want all uploaded files to go to a specific Google Drive folder:

1. Create a folder in Google Drive
2. Right-click on the folder and select "Get link"
3. Copy the folder ID from the URL (the long string after `/folders/`)
4. Add it to your configuration as `google.drive.folder.id`

## Testing the Integration

### 1. Start the Application

Run the application with the updated configuration:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. Test File Upload

Use the API to test file upload with Google Drive storage:

```bash
curl -X POST "http://localhost:8081/cra-api/api/soli-arquivos/upload" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/your/file.pdf" \
  -F "solicitacaoId=1" \
  -F "origem=usuario" \
  -F "storageLocation=google_drive"
```

### 3. Verify in Google Drive

Check your Google Drive to see if the file was uploaded successfully.

## Troubleshooting Common Issues

### 1. Authentication Errors

If you get authentication errors:

1. Verify that your Client ID and Client Secret are correct
2. Ensure the Google Drive API is enabled for your project
3. Check that your credentials are for the correct application type

### 2. Network Connectivity Issues

If you experience network issues:

1. Check your internet connection
2. Verify that your firewall isn't blocking outbound connections
3. Test connectivity to Google's servers with:
   ```bash
   ping google.com
   telnet www.googleapis.com 443
   ```

### 3. SSL/TLS Issues

If you encounter SSL/TLS errors:

1. Update your Java installation to the latest version
2. Try adding these JVM arguments:
   ```bash
   -Djavax.net.ssl.trustStore=NUL -Djavax.net.ssl.trustStoreType=Windows-ROOT
   ```

### 4. Permission Issues

If you get permission errors:

1. Ensure your Google account has permission to create files in Google Drive
2. Check that the folder ID (if specified) is accessible to your account

## Production Considerations

### 1. Service Account vs OAuth2

For production use, it's recommended to use a service account instead of OAuth2 client credentials:

1. In the Google Cloud Console, go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "Service account"
3. Follow the prompts to create a service account
4. Create and download a JSON key file for the service account
5. Update your configuration:
   ```properties
   # Enable Google Drive OAuth integration
   google.drive.oauth.enabled=true
   
   # Google Drive OAuth client credentials (not used with service account)
   google.drive.oauth.client.id=
   google.drive.oauth.client.secret=
   
   # Google Drive folder ID (optional)
   google.drive.folder.id=YOUR_FOLDER_ID_HERE
   
   # Service Account Key Path
   google.service.account.key.path=/path/to/your/service-account-key.json
   ```

### 2. Security Best Practices

1. Store credentials securely (use environment variables or secure configuration management)
2. Limit the permissions of service accounts to only what's necessary
3. Regularly rotate credentials
4. Monitor API usage for unusual activity

### 3. Monitoring and Logging

1. Implement logging for Google Drive operations
2. Set up alerts for persistent failures
3. Monitor retry attempts to detect ongoing issues

## Fallback Behavior

The system is designed with graceful degradation:

1. When Google Drive operations fail, files are automatically saved to local storage
2. Users can still access files even when Google Drive is unavailable
3. The system logs detailed information about failures for troubleshooting

## API Usage Examples

### Upload to Google Drive

```bash
curl -X POST "http://localhost:8081/cra-api/api/soli-arquivos/upload" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/your/file.pdf" \
  -F "solicitacaoId=1" \
  -F "origem=usuario" \
  -F "storageLocation=google_drive"
```

### Upload to Local Storage

```bash
curl -X POST "http://localhost:8081/cra-api/api/soli-arquivos/upload" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/your/file.pdf" \
  -F "solicitacaoId=1" \
  -F "origem=usuario" \
  -F "storageLocation=local"
```

## Summary

This setup guide provides everything you need to configure Google Drive integration for the CRA Backend system. The implementation includes:

1. Robust error handling with retry logic
2. Graceful degradation to local storage
3. Comprehensive logging for troubleshooting
4. Flexible configuration options

With proper configuration, the system will provide reliable file storage with automatic fallback when needed.