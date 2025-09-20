# Simplified Google Drive Integration Setup

## Overview

This document explains how to properly configure the simplified Google Drive integration for the CRA Backend system. The current implementation in [GoogleDriveService.java](src/main/java/br/adv/cra/service/GoogleDriveService.java) is a placeholder that needs to be properly implemented with valid authentication.

## Current Implementation Status

The current Google Drive service implementation is a simplified version that:

1. Handles timeout issues to prevent hanging
2. Provides graceful fallback to local storage when Google Drive is not available
3. Has placeholder authentication that needs to be replaced with a proper implementation

## Required Setup Steps

### 1. Google Cloud Console Configuration

To use Google Drive integration, you need to set up a project in the Google Cloud Console:

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Drive API:
   - Navigate to "APIs & Services" > "Library"
   - Search for "Google Drive API"
   - Click on it and press "Enable"

### 2. OAuth2 Client Credentials

For the simplified implementation, you'll need to create OAuth2 client credentials:

1. In the Google Cloud Console, go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "OAuth client ID"
3. Select "Desktop application" as the application type
4. Note the Client ID and Client Secret

### 3. Configuration in application.properties

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

## Implementation Options

### Option 1: Service Account (Recommended for Production)

For production use, it's recommended to use a service account:

1. In the Google Cloud Console, go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "Service account"
3. Follow the prompts to create a service account
4. Create and download a JSON key file for the service account
5. Update the configuration:

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

### Option 2: OAuth2 Client Credentials (Development/Testing)

For development and testing, you can use OAuth2 client credentials:

1. Follow the steps in the "OAuth2 Client Credentials" section above
2. Update the configuration with your client ID and secret

## Important Notes

### Authentication Flow

The current simplified implementation does not handle the OAuth2 authentication flow. In a complete implementation, you would need to:

1. Redirect users to Google's OAuth2 consent screen
2. Handle the callback with the authorization code
3. Exchange the authorization code for access and refresh tokens
4. Store and manage these tokens securely

### Error Handling

The current implementation includes robust error handling that:

1. Prevents the service from hanging during HTTP transport creation
2. Provides clear error messages for different failure scenarios
3. Gracefully falls back to local storage when Google Drive is not available

### Timeout Configuration

The service includes timeout configuration to prevent hanging:

- HTTP transport creation timeout: 15 seconds
- Connection timeout: 30 seconds
- Read timeout: 30 seconds

## Troubleshooting

### Common Issues

1. **Timeout Errors**: Check your network connection and firewall settings
2. **Authentication Errors**: Verify your client credentials and ensure the Google Drive API is enabled
3. **Permission Errors**: Ensure your service account or OAuth2 credentials have the necessary permissions

### Network Issues

If you're experiencing network-related issues:

1. Check that you can reach Google's servers from your environment
2. Verify that your firewall is not blocking the connection
3. Test with a simple network connectivity tool

### Fallback Behavior

The system is designed to gracefully fall back to local storage when Google Drive is not available, ensuring that file operations continue to work even when Google Drive integration fails.

## Next Steps

To fully implement Google Drive integration, you'll need to:

1. Implement proper authentication (either service account or OAuth2 flow)
2. Handle token management (storage, refresh, etc.)
3. Implement proper error handling for authentication failures
4. Test the integration thoroughly in your environment

The current implementation provides a solid foundation with proper error handling and fallback mechanisms.