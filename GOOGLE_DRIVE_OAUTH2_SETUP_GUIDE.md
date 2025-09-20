# Google Drive OAuth2 Authentication Setup Guide

This guide explains how to properly set up Google Drive OAuth2 authentication for the CRA Backend application.

## Overview

The application now uses proper OAuth2 authentication with Google Drive, which requires users to authenticate before they can upload files. This resolves the 401 Unauthorized errors that were occurring previously.

## Authentication Flow

The authentication flow consists of the following steps:

1. **Initiate Authorization**: User calls the `/api/google-drive/authorize` endpoint
2. **User Authentication**: User authenticates with their Google account
3. **Callback**: Google redirects back to the application with an authorization code
4. **Token Exchange**: Application exchanges the authorization code for access and refresh tokens
5. **Token Storage**: Tokens are stored in the database for future use
6. **File Operations**: User can now upload/download/delete files using their stored credentials

## Step-by-Step Setup

### 1. Configure Google Cloud Project

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Drive API:
   - Navigate to "APIs & Services" > "Library"
   - Search for "Google Drive API"
   - Click on it and press "Enable"
4. Create OAuth2 credentials:
   - Navigate to "APIs & Services" > "Credentials"
   - Click "Create Credentials" > "OAuth client ID"
   - Select "Web application"
   - Set the authorized redirect URIs to match your application's callback URL:
     - For local development: `http://localhost:8080/api/google-drive/callback`
     - For production: `https://your-domain.com/api/google-drive/callback`
   - Note the Client ID and Client Secret

### 2. Configure Application Properties

Add the following properties to your `application.properties` or `application.yml`:

```properties
# Google Drive OAuth2 Configuration
google.drive.oauth.client.id=YOUR_GOOGLE_CLIENT_ID
google.drive.oauth.client.secret=YOUR_GOOGLE_CLIENT_SECRET
google.drive.oauth.redirect.uri=http://localhost:8080/api/google-drive/callback
google.drive.oauth.enabled=true

# Optional: Google Drive folder ID to store files
google.drive.folder.id=YOUR_OPTIONAL_FOLDER_ID
```

### 3. Authenticate a User

Before a user can upload files to Google Drive, they must authenticate with their Google account:

1. **Get Authorization URL**:
   ```http
   GET /api/google-drive/authorize?userId={userId}
   ```
   
   Response:
   ```json
   {
     "authorizationUrl": "https://accounts.google.com/oauth/authorize?..."
   }
   ```

2. **Redirect User**: Redirect the user to the authorization URL to authenticate with Google

3. **Handle Callback**: After the user authenticates, Google will redirect to the callback URL with an authorization code

4. **Token Storage**: The application automatically exchanges the authorization code for tokens and stores them in the database

### 4. Verify Connection Status

Check if a user has already connected their Google Drive account:

```http
GET /api/google-drive/status?userId={userId}
```

Response:
```json
{
  "connected": true,
  "userId": 123,
  "message": "Google Drive is connected"
}
```

### 5. Disconnect Account (Optional)

To disconnect a user's Google Drive account:

```http
DELETE /api/google-drive/disconnect?userId={userId}
```

Response:
```json
{
  "message": "Google Drive disconnected successfully",
  "userId": "123"
}
```

## File Upload Process

After a user has authenticated, they can upload files to Google Drive:

1. **Upload File**:
   ```http
   POST /api/soli-arquivos/upload
   Content-Type: multipart/form-data
   
   file: [file data]
   solicitacaoId: 123
   origem: usuario
   storageLocation: google_drive
   ```

2. **The application will**:
   - Retrieve the user's stored credentials from the database
   - Use the credentials to authenticate with Google Drive
   - Upload the file using the authenticated connection

## Troubleshooting

### Common Issues

1. **401 Unauthorized Errors**:
   - Ensure the user has authenticated with Google Drive
   - Check that the OAuth2 credentials are correctly configured
   - Verify that the Google Drive API is enabled in the Google Cloud Console

2. **Invalid Credentials**:
   - The stored tokens may have expired
   - The user may have revoked access to the application
   - Re-authenticate the user to get new tokens

3. **Redirect URI Mismatch**:
   - Ensure the redirect URI in Google Cloud Console matches the application's callback URL
   - The URI must be exactly the same, including protocol (http/https) and port

### Testing Authentication

You can test the authentication flow using the following steps:

1. Call the authorize endpoint:
   ```bash
   curl "http://localhost:8080/api/google-drive/authorize?userId=1"
   ```

2. Follow the authorization URL in a browser to authenticate

3. After authentication, check the connection status:
   ```bash
   curl "http://localhost:8080/api/google-drive/status?userId=1"
   ```

## Security Considerations

1. **Token Storage**: Access and refresh tokens are stored in the database
2. **User Isolation**: Each user's tokens are stored separately
3. **Token Refresh**: The application automatically refreshes expired tokens when possible
4. **Encryption**: Consider encrypting stored tokens in production environments

## Conclusion

With proper OAuth2 authentication, users can now securely upload files to their Google Drive accounts. The application handles token storage and refresh automatically, providing a seamless experience for users.