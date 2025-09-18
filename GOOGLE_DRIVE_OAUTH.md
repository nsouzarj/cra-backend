# Google Drive OAuth Integration

This document explains how to configure and use the Google Drive OAuth integration in the CRA Backend application.

## Overview

The CRA Backend supports storing file attachments in Google Drive using OAuth 2.0 authentication. When enabled, files uploaded through the application will be stored in Google Drive instead of the local filesystem.

## Configuration

### Enabling Google Drive OAuth

To enable Google Drive OAuth integration, set the following properties in your application configuration:

```properties
# Enable Google Drive OAuth integration
google.drive.oauth.enabled=true

# Google Drive OAuth client credentials
google.drive.oauth.client.id=your-client-id
google.drive.oauth.client.secret=your-client-secret

# Optional: Google Drive folder ID to store files in a specific folder
google.drive.folder.id=your-folder-id
```

### Environment-specific Configuration

#### Development Environment (application-dev.properties)
```properties
# Enable Google Drive OAuth integration
google.drive.oauth.enabled=false
# Set your client credentials
google.drive.oauth.client.id=
google.drive.oauth.client.secret=
```

#### Production Environment (application-prod.properties)
```properties
# Enable Google Drive OAuth integration
google.drive.oauth.enabled=false
# Use environment variables for credentials
google.drive.oauth.client.id=${GOOGLE_DRIVE_CLIENT_ID}
google.drive.oauth.client.secret=${GOOGLE_DRIVE_CLIENT_SECRET}
# Optional folder ID
google.drive.folder.id=${GOOGLE_DRIVE_FOLDER_ID}
```

## Setting up Google OAuth Credentials

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Drive API for your project
4. Go to "Credentials" and create an OAuth 2.0 Client ID
5. Set the application type to "Web application"
6. Add authorized redirect URIs:
   - For development: `http://localhost:8081/callback`
   - For production: `https://yourdomain.com/callback`
7. Save the client ID and client secret

## Using Environment Variables (Recommended for Production)

For security reasons, it's recommended to use environment variables for storing sensitive credentials:

```bash
export GOOGLE_DRIVE_CLIENT_ID="your-client-id"
export GOOGLE_DRIVE_CLIENT_SECRET="your-client-secret"
export GOOGLE_DRIVE_FOLDER_ID="your-folder-id"  # Optional
```

## How it Works

When Google Drive OAuth is enabled and properly configured:

1. Files uploaded through the `/api/soli-arquivos/upload` endpoint are automatically stored in Google Drive
2. The file metadata is still stored in the database
3. When downloading files, the application retrieves them from Google Drive
4. When deleting files, the application removes them from Google Drive

If Google Drive OAuth is disabled or not properly configured, the application falls back to local filesystem storage.

## Testing

To test the Google Drive integration:

1. Enable the integration in your development environment
2. Set your client credentials
3. Run the application
4. Upload a file through the API
5. Check that the file appears in your Google Drive

## Troubleshooting

### Common Issues

1. **Authentication errors**: Make sure your client credentials are correct and the Google Drive API is enabled
2. **Permission errors**: Ensure your OAuth client has the necessary scopes for Google Drive access
3. **Network issues**: Check that your application can reach Google's servers

### Logs

Check the application logs for detailed error messages:
- Look for entries with "Google Drive OAuth" in the message
- Enable DEBUG logging for the `br.adv.cra.service` package for more detailed information

## Security Considerations

1. Never commit client credentials to version control
2. Use environment variables or secure configuration management systems
3. Regularly rotate your credentials
4. Monitor API usage in the Google Cloud Console