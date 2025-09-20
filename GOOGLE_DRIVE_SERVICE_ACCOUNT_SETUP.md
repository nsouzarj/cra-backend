# Google Drive Service Account Setup Guide

## Overview
This guide explains how to set up Google Drive integration using a service account, which is the recommended approach for server-to-server authentication in production environments.

## Why Use Service Accounts?

Service accounts provide several advantages over OAuth2 for server applications:
- No manual token management required
- Automatic token refresh
- Simpler configuration
- Better security through key-based authentication
- More reliable for automated processes

## Step-by-Step Setup

### 1. Create a Google Cloud Project

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Click "Select a project" then "New Project"
3. Enter a project name (e.g., "cra-backend")
4. Click "Create"

### 2. Enable the Google Drive API

1. In the Google Cloud Console, make sure your project is selected
2. Go to "APIs & Services" > "Library"
3. Search for "Google Drive API"
4. Click on "Google Drive API"
5. Click "Enable"

### 3. Create a Service Account

1. Go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "Service account"
3. Fill in the service account details:
   - **Service account name**: e.g., "cra-backend-service"
   - **Service account ID**: Will be auto-generated
   - **Service account description**: e.g., "Service account for CRA Backend Google Drive integration"
4. Click "Create and Continue"

### 4. Grant Service Account Permissions

1. For "Select a role", choose:
   - **Role**: "Basic" > "Editor" (or create a custom role with only Drive API permissions)
2. Click "Continue"
3. For "Grant users access to this service account", leave blank
4. Click "Done"

### 5. Create and Download Service Account Key

1. On the Credentials page, find your service account
2. Click the three dots menu next to it and select "Manage keys"
3. Click "Add Key" > "Create new key"
4. Select "JSON" as the key type
5. Click "Create"
6. The JSON key file will be downloaded automatically
7. **Important**: Store this file securely and never commit it to version control

### 6. Configure Google Drive Folder

1. Create a folder in Google Drive where files will be stored
2. Copy the folder ID from the URL (the long string after `/folders/`)
3. Share the folder with the service account:
   - Right-click the folder in Google Drive
   - Select "Share"
   - Add the service account email (found in the JSON key file)
   - Set permissions to "Editor" or "Contributor"

### 7. Configure Application

#### Option A: Using Environment Variables (Recommended)

Set these environment variables:

**Windows (Command Prompt)**:
```cmd
set GOOGLE_SERVICE_ACCOUNT_KEY_PATH=C:\secure\path\to\your-service-account-key.json
set GOOGLE_DRIVE_FOLDER_ID=your-folder-id-from-step-6
```

**Windows (PowerShell)**:
```powershell
$env:GOOGLE_SERVICE_ACCOUNT_KEY_PATH="C:\secure\path\to\your-service-account-key.json"
$env:GOOGLE_DRIVE_FOLDER_ID="your-folder-id-from-step-6"
```

**Linux/macOS**:
```bash
export GOOGLE_SERVICE_ACCOUNT_KEY_PATH="/secure/path/to/your-service-account-key.json"
export GOOGLE_DRIVE_FOLDER_ID="your-folder-id-from-step-6"
```

#### Option B: Direct Configuration in application.properties

Update your `application.properties`:

```properties
# Enable Google Drive integration
google.drive.oauth.enabled=true

# Service Account Key Path
google.service.account.key.path=C:/secure/path/to/your-service-account-key.json

# Google Drive folder ID
google.drive.folder.id=your-folder-id-from-step-6
```

**Note**: Never commit the service account key file path or folder ID to version control.

### 8. Test the Integration

1. Start your application
2. Try uploading a file with `storageLocation=google_drive`
3. Check that the file appears in your Google Drive folder

## Security Best Practices

### Key File Security
- Store the JSON key file in a secure location outside the project directory
- Set appropriate file permissions (read-only for the application user)
- Never commit the key file to version control
- Add the key file to `.gitignore`:
  ```
  # Google Service Account Keys
  *.json
  ```

### Environment-Specific Configuration
- Use different service accounts for development, testing, and production
- Rotate service account keys periodically
- Monitor service account usage in Google Cloud Console

### Access Control
- Grant the minimum necessary permissions to the service account
- Use a dedicated Google Drive folder for the application
- Regularly review folder sharing settings

## Troubleshooting

### Common Issues

1. **"invalid_grant" error**:
   - Check that the service account key file is valid
   - Verify the file path is correct
   - Ensure the file has not been deleted or rotated

2. **"Insufficient Permission" error**:
   - Verify the service account has been shared with the Google Drive folder
   - Check that the folder ID is correct
   - Ensure the service account has Editor permissions

3. **"File not found" error**:
   - Verify the folder ID format
   - Check that the folder still exists
   - Ensure the service account still has access

### Debugging Steps

1. **Enable detailed logging**:
   ```properties
   logging.level.br.adv.cra.service.GoogleDriveService=DEBUG
   logging.level.com.google.api.client=DEBUG
   ```

2. **Test service account key**:
   - Verify the JSON file is valid
   - Check that it contains the expected fields (client_email, private_key, etc.)

3. **Verify folder access**:
   - Manually check that the service account email has access to the folder
   - Test sharing with the service account email again

## Sample Service Account Key Structure

A service account key JSON file looks like this:

```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "your-private-key-id",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "your-service-account@your-project-id.iam.gserviceaccount.com",
  "client_id": "your-client-id",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/..."
}
```

## Additional Resources

- [Google Cloud Service Accounts Documentation](https://cloud.google.com/iam/docs/service-accounts)
- [Google Drive API Documentation](https://developers.google.com/drive/api)
- [Google API Client Libraries](https://github.com/googleapis/google-api-java-client)