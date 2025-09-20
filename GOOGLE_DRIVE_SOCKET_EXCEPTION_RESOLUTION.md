# Google Drive SocketException Resolution Guide

## Problem Description

You encountered the following error:
```
java.net.SocketException: An established connection was aborted by the software in your host machine
```

This error occurred during a Google Drive file upload operation in the `GoogleDriveService.uploadFile()` method.

## Root Cause Analysis

This error is a network-level issue that occurs during the SSL/TLS handshake process when trying to establish a connection to Google's servers. It can happen for several reasons:

1. **Network connectivity issues** - Unstable internet connection
2. **Firewall/proxy blocking** - Security software interfering with the connection
3. **SSL/TLS configuration problems** - Certificate or protocol compatibility issues
4. **Google API service temporary issues** - Temporary outages on Google's side

## Solution Implemented

I've implemented several improvements to make the Google Drive integration more robust:

### 1. Retry Logic with Exponential Backoff

The updated [GoogleDriveService.java](src/main/java/br/adv/cra/service/GoogleDriveService.java) now includes retry logic for all operations (upload, download, delete):

```java
private File uploadWithRetry(Drive service, File fileMetadata, InputStreamContent mediaContent) throws IOException {
    int maxRetries = 3;
    int retryCount = 0;
    
    while (retryCount < maxRetries) {
        try {
            return service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        } catch (IOException e) {
            retryCount++;
            if (retryCount >= maxRetries) {
                logger.error("Max retries reached for Google Drive upload after {} attempts", maxRetries);
                throw e;
            }
            
            // Check if this is a network-related error that might be recoverable
            if (isRecoverableNetworkError(e)) {
                logger.warn("Recoverable network error occurred during Google Drive upload, retrying... (attempt {}/{})", retryCount, maxRetries);
                try {
                    Thread.sleep(1000 * retryCount); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted during retry", ie);
                }
            } else {
                // Non-recoverable error, don't retry
                logger.error("Non-recoverable error during Google Drive upload: ", e);
                throw e;
            }
        }
    }
    
    throw new IOException("Failed to upload file to Google Drive after " + maxRetries + " attempts");
}
```

### 2. Network Error Detection

The service now detects common network-related errors and treats them as recoverable:

```java
private boolean isRecoverableNetworkError(IOException e) {
    String message = e.getMessage();
    if (message == null) return false;
    
    // Check for common network-related error messages
    return message.contains("SocketException") || 
           message.contains("Connection reset") ||
           message.contains("Connection aborted") ||
           message.contains("timeout") ||
           message.contains("SSL") ||
           message.contains("handshake");
}
```

### 3. Graceful Degradation

The [SoliArquivoService.java](src/main/java/br/adv/cra/service/SoliArquivoService.java) already had graceful degradation built-in, which will continue to work:
- When Google Drive operations fail, the system automatically falls back to local storage
- Users will still be able to upload/download files even when Google Drive is unavailable

## Immediate Troubleshooting Steps

### 1. Check Network Connectivity

Verify that your server can reach Google's servers:

```bash
# Test basic connectivity to Google
ping google.com

# Test HTTPS connectivity to Google APIs
telnet www.googleapis.com 443
```

### 2. Check Firewall and Security Software

Ensure that your firewall or security software is not blocking outbound HTTPS connections to Google's servers:
- Allow outbound connections to `*.googleapis.com` on port 443
- Check if any proxy settings are needed

### 3. Update Java SSL Configuration

If you're still experiencing SSL issues, you might need to update your Java installation or add JVM arguments:

```bash
# Add these JVM arguments to bypass SSL issues (for testing only)
-Djavax.net.ssl.trustStore=NUL -Djavax.net.ssl.trustStoreType=Windows-ROOT
```

### 4. Test with a Simple Network Tool

You can test connectivity with a simple curl command:

```bash
curl -v https://www.googleapis.com/drive/v3/about?fields=kind
```

## Long-term Solutions

### 1. Implement Proper Authentication

The current implementation uses a simplified approach without proper OAuth2 authentication. For production use:

1. Implement a complete OAuth2 flow
2. Obtain valid access tokens
3. Handle token refresh automatically

### 2. Configure Proxy Settings (if needed)

If you're behind a corporate proxy, configure it in your application:

```java
// In your GoogleDriveService, you might need to configure proxy settings
System.setProperty("https.proxyHost", "your.proxy.host");
System.setProperty("https.proxyPort", "proxy_port");
System.setProperty("https.proxyUser", "username"); // if authentication required
System.setProperty("https.proxyPassword", "password"); // if authentication required
```

### 3. Monitor Network Health

Implement monitoring to detect when network issues occur:
- Log connection failures
- Track retry attempts
- Alert on persistent failures

## Configuration Updates

Make sure your [application.properties](src/main/resources/application.properties) has the correct Google Drive configuration:

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

## Summary

The implemented solution provides:

1. **Improved Reliability**: Retry logic with exponential backoff handles transient network issues
2. **Better Error Handling**: Specific detection of network-related errors
3. **Graceful Degradation**: Automatic fallback to local storage when Google Drive is unavailable
4. **Enhanced Logging**: Detailed logging to help with troubleshooting

These changes should significantly reduce the occurrence of SocketException errors and improve the overall reliability of the Google Drive integration.