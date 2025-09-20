# Google Drive Integration Usage Guide

## Overview
This guide explains how to use the Google Drive integration in the CRA Backend system, including authentication, status checking, and file operations.

## Authentication Flow

### 1. Get Authorization URL
To start the authentication process, get the authorization URL:

```http
GET /api/google-drive/auth-url
```

This returns a plain text URL that you should open in a browser to authenticate with Google.

### 2. Complete Authentication
After opening the URL in your browser:
1. Sign in with your Google account
2. Grant the necessary permissions to the application
3. Google will redirect to the callback URL with an authorization code

### 3. Token Storage
The application automatically exchanges the authorization code for access and refresh tokens, which are stored in memory.

## Checking Status

### Token Status
Check if tokens are configured and valid:

```http
GET /api/google-drive/token-status
```

Response:
```json
{
  "hasTokens": true,
  "hasValidTokens": true,
  "accessToken": "SET",
  "refreshToken": "SET",
  "message": "Google Drive tokens are configured",
  "status": "VALID"
}
```

### Connection Status
Check if Google Drive is available and properly authenticated:

```http
GET /api/google-drive/status
```

Response:
```json
{
  "message": "Google Drive service is available and properly authenticated",
  "status": "OK"
}
```

## File Operations

### Upload File
To upload a file to Google Drive, specify `storageLocation=google_drive`:

```http
POST /api/soli-arquivos/upload
Content-Type: multipart/form-data

file: [file data]
solicitacaoId: [solicitation ID]
origem: usuario
storageLocation: google_drive
```

To upload a file locally, specify `storageLocation=local` or omit the parameter:

```http
POST /api/soli-arquivos/upload
Content-Type: multipart/form-data

file: [file data]
solicitacaoId: [solicitation ID]
origem: usuario
storageLocation: local
```

### Storage Location Behavior

1. **Local Storage** (`storageLocation=local`):
   - No authentication required
   - Files are stored on the local filesystem
   - Works even when Google Drive is not configured or unavailable

2. **Google Drive Storage** (`storageLocation=google_drive`):
   - Requires valid Google Drive authentication
   - Files are stored in the user's Google Drive account
   - Returns an error if authentication is missing or invalid (no fallback to local storage)

### Error Handling
If tokens are missing or invalid when using Google Drive storage, you'll receive a 500 error with a descriptive message:
```json
{
  "error": "Google Drive authentication is required but not available. Please authenticate with Google Drive first."
}
```

In this case, you need to repeat the authentication flow.

## Troubleshooting

### Common Issues

1. **401 Unauthorized Errors**
   - Cause: Missing or expired tokens
   - Solution: Re-authenticate with Google Drive

2. **Token Refresh Failures**
   - Cause: Invalid refresh token
   - Solution: Re-authenticate with Google Drive

3. **Network Issues**
   - Cause: Connectivity problems to Google's servers
   - Solution: Check network connectivity and firewall settings

### Testing Authentication
You can test the complete flow using the provided batch script:
```
test-google-drive-auth.bat
```

## Best Practices

1. **Storage Location Selection**
   - Use local storage for reliability when Google Drive is not critical
   - Use Google Drive storage when you want files to be stored in the user's Google Drive account
   - Always check authentication status before using Google Drive storage

2. **Regular Status Checks**
   - Check token status before file operations that use Google Drive
   - Monitor connection status for proactive issue detection

3. **Error Handling**
   - Always handle authentication errors by redirecting users to re-authenticate
   - Implement retry logic for transient network issues

4. **User Experience**
   - Provide clear instructions for the authentication process
   - Inform users when re-authentication is needed
   - Give users the choice between local and Google Drive storage