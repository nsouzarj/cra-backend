# Google Drive OAuth2 Authentication Flow Example

This document provides a step-by-step example of how to authenticate with Google Drive and upload files using the CRA Backend application.

## Prerequisites

1. Google Cloud Project with Google Drive API enabled
2. OAuth2 credentials (Client ID and Client Secret)
3. Properly configured `application.properties` with Google Drive settings

## Authentication Flow

### Step 1: Initiate Google Drive Authorization

First, the user needs to authenticate with Google Drive. Call the authorization endpoint:

```http
GET /api/google-drive/authorize?userId={user-id}
```

Example:
```bash
curl "http://localhost:8081/cra-api/api/google-drive/authorize?userId=1"
```

Response:
```json
{
  "authorizationUrl": "https://accounts.google.com/oauth/authorize?client_id=...&redirect_uri=...&response_type=code&scope=https://www.googleapis.com/auth/drive.file&state=1"
}
```

### Step 2: User Authentication

Open the `authorizationUrl` in a web browser. The user will be prompted to:
1. Sign in to their Google account
2. Grant permission to the application to access their Google Drive files

### Step 3: OAuth2 Callback

After the user grants permission, Google will redirect to the callback URL with an authorization code:

```
GET /api/google-drive/callback?code={authorization-code}&state={user-id}
```

The application automatically exchanges this code for access and refresh tokens, which are stored in the database.

### Step 4: Verify Connection

Check if the user is connected to Google Drive:

```http
GET /api/google-drive/status?userId={user-id}
```

Example:
```bash
curl "http://localhost:8081/cra-api/api/google-drive/status?userId=1"
```

Response:
```json
{
  "connected": true,
  "userId": 1,
  "message": "Google Drive is connected"
}
```

## File Upload Process

### Step 1: Upload File to Google Drive

Once authenticated, users can upload files to Google Drive:

```http
POST /api/soli-arquivos/upload
Content-Type: multipart/form-data

file: [file data]
solicitacaoId: 123
origem: usuario
storageLocation: google_drive
```

Example using curl:
```bash
curl -X POST "http://localhost:8081/cra-api/api/soli-arquivos/upload" \
  -H "Authorization: Bearer {jwt-token}" \
  -F "file=@/path/to/your/file.pdf" \
  -F "solicitacaoId=123" \
  -F "origem=usuario" \
  -F "storageLocation=google_drive"
```

### Step 2: File Operations

The application will:
1. Retrieve the authenticated user's stored credentials from the database
2. Use those credentials to authenticate with Google Drive
3. Upload the file to the user's Google Drive
4. Store file metadata in the database with the Google Drive file ID

## File Download Process

To download a file stored in Google Drive:

```http
GET /api/soli-arquivos/{file-id}/download
```

The application will:
1. Retrieve the file metadata from the database
2. Use the stored user credentials to authenticate with Google Drive
3. Download the file content and return it to the client

## File Deletion Process

To delete a file stored in Google Drive:

```http
DELETE /api/soli-arquivos/{file-id}?origem=usuario
```

The application will:
1. Retrieve the file metadata from the database
2. Use the stored user credentials to authenticate with Google Drive
3. Delete the file from Google Drive
4. Remove the file metadata from the database

## Token Refresh

The application automatically handles token refresh:
- When a user's access token expires, the application uses the refresh token to obtain a new access token
- Updated tokens are stored in the database
- This process is transparent to the user

## Error Handling

Common error scenarios and how they're handled:

1. **401 Unauthorized**: User hasn't authenticated with Google Drive
   - Solution: Complete the OAuth2 flow first

2. **Token Expired**: Access token has expired and refresh failed
   - Solution: Re-authenticate the user

3. **Network Issues**: Temporary connectivity problems
   - Solution: The application retries with exponential backoff

4. **Rate Limiting**: Google Drive API rate limits exceeded
   - Solution: Requests are retried after a delay

## Security Considerations

1. **Token Storage**: Access and refresh tokens are stored securely in the database
2. **User Isolation**: Each user's tokens are stored separately and can only be used by that user
3. **Encryption**: In production, consider encrypting stored tokens
4. **Scope Limitation**: The application requests minimal required permissions (drive.file scope)

## Testing

To test the Google Drive integration:

1. Ensure Google Drive is enabled in application.properties:
   ```properties
   google.drive.oauth.enabled=true
   ```

2. Configure OAuth2 credentials:
   ```properties
   google.drive.oauth.client.id=your-client-id
   google.drive.oauth.client.secret=your-client-secret
   google.drive.oauth.redirect.uri=http://localhost:8081/cra-api/api/google-drive/callback
   ```

3. Follow the authentication flow described above
4. Upload, download, and delete files to verify functionality

## Troubleshooting

If you encounter issues:

1. **Check Logs**: Look for error messages in the application logs
2. **Verify Credentials**: Ensure Client ID and Client Secret are correct
3. **Check Redirect URI**: Ensure it matches the one configured in Google Cloud Console
4. **Test Connectivity**: Verify network connectivity to Google's servers
5. **Review Permissions**: Ensure the Google Drive API is enabled for your project