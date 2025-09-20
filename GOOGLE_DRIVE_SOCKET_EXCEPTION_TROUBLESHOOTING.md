# Google Drive SocketException Troubleshooting

## Overview

This document provides troubleshooting steps for resolving `java.net.SocketException: An established connection was aborted by the software in your host machine` errors when using the Google Drive integration.

## Understanding the Error

The SocketException typically occurs when:

1. Network connectivity issues between your application and Google's servers
2. Firewall or security software blocking the connection
3. Proxy configuration issues
4. SSL/TLS handshake problems
5. Google API service temporary issues

## Troubleshooting Steps

### 1. Network Connectivity Check

First, verify that your machine can reach Google's servers:

```bash
# Test basic connectivity to Google
ping google.com

# Test HTTPS connectivity to Google APIs
telnet www.googleapis.com 443
```

If these tests fail, check your network configuration.

### 2. Firewall and Security Software

Check if your firewall or security software is blocking the connection:

1. Temporarily disable firewall/antivirus to test
2. Add an exception for your Java application
3. Ensure outbound HTTPS (port 443) traffic is allowed

### 3. Proxy Configuration

If you're behind a corporate proxy, you may need to configure it:

```java
// In your GoogleDriveService, you might need to configure proxy settings
System.setProperty("https.proxyHost", "your.proxy.host");
System.setProperty("https.proxyPort", "proxy_port");
System.setProperty("https.proxyUser", "username"); // if authentication required
System.setProperty("https.proxyPassword", "password"); // if authentication required
```

### 4. SSL/TLS Configuration

Ensure your Java environment has up-to-date SSL certificates:

1. Update your JDK/JRE to the latest version
2. Check that your Java keystore has current certificates
3. Try adding these JVM arguments to bypass SSL issues (for testing only):

```bash
-Djavax.net.ssl.trustStore=NUL -Djavax.net.ssl.trustStoreType=Windows-ROOT
```

### 5. Google API Service Status

Check if there are any ongoing issues with Google Drive API:

1. Visit [Google Cloud Status Dashboard](https://status.cloud.google.com/)
2. Check for any reported issues with Google Drive API

## Code-Level Solutions

### 1. Enhanced Timeout Configuration

The current implementation already includes timeout handling, but you can adjust the values:

```java
// In GoogleDriveService.java, adjust timeout values
private HttpRequestInitializer setHttpTimeout(final GoogleCredential credential) {
    return new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest httpRequest) throws IOException {
            credential.initialize(httpRequest);
            httpRequest.setConnectTimeout(60000); // 60 seconds (increased from 30)
            httpRequest.setReadTimeout(60000);    // 60 seconds (increased from 30)
            logger.debug("HTTP timeout settings applied: connect=60s, read=60s");
        }
    };
}
```

### 2. Retry Logic

Implement retry logic for transient network errors:

```java
// Add retry logic to GoogleDriveService methods
public String uploadFile(MultipartFile file) throws IOException {
    int maxRetries = 3;
    int retryCount = 0;
    
    while (retryCount < maxRetries) {
        try {
            logger.info("Starting Google Drive file upload for file: {}", file.getOriginalFilename());
            
            // Create Google Drive service
            Drive service = createDriveService();
            
            // ... rest of upload logic
            
            return uploadedFile.getId();
        } catch (SocketException e) {
            retryCount++;
            if (retryCount >= maxRetries) {
                logger.error("Max retries reached for Google Drive upload");
                throw e;
            }
            logger.warn("SocketException occurred, retrying... (attempt {}/{})", retryCount, maxRetries);
            try {
                Thread.sleep(1000 * retryCount); // Exponential backoff
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted during retry", ie);
            }
        }
        // ... other catch blocks
    }
    throw new IOException("Failed to upload file after " + maxRetries + " attempts");
}
```

### 3. Connection Pooling

Consider implementing connection pooling for better resource management:

```java
// Add connection pooling configuration
private Drive createDriveService() throws GeneralSecurityException, IOException {
    
    // Configure connection pooling
    HTTP_TRANSPORT.setConnectionFactory(new DefaultConnectionFactory() {
        @Override
        public HttpURLConnection openConnection(URL url) throws IOException {
            HttpURLConnection connection = super.openConnection(url);
            connection.setRequestProperty("Connection", "keep-alive");
            return connection;
        }
    });
    
    // ... rest of method
}
```

## System-Level Solutions

### 1. Windows-Specific Fixes

Since you're using Windows 24H2, try these solutions:

1. Reset network settings:
   ```cmd
   netsh winsock reset
   netsh int ip reset
   ```

2. Flush DNS cache:
   ```cmd
   ipconfig /flushdns
   ```

3. Renew IP configuration:
   ```cmd
   ipconfig /renew
   ```

### 2. Java Version Compatibility

Ensure you're using a compatible Java version:

1. Check your Java version:
   ```cmd
   java -version
   ```

2. Consider using Java 11 or later for better network stack support

### 3. Windows Update Issues

Windows 24H2 might have networking issues:

1. Check for pending Windows updates
2. Install all available updates
3. Restart your machine after updates

## Monitoring and Logging

Enhance logging to better understand when and why the SocketException occurs:

```java
// In SoliArquivoService.java, enhance error logging
} catch (SocketException e) {
    logger.error("Network error during Google Drive operation: ", e);
    logger.error("SocketException details - Message: {}, Error code: {}, Local port: {}, Remote host: {}", 
                 e.getMessage(), 
                 e.getErrorCode(), 
                 e.getPort(), 
                 e.getDestinationAddress());
    logger.warn("Failed to save file to Google Drive due to network issues. Falling back to local storage.");
    return saveFileLocally(file, solicitacao, origem);
}
```

## Prevention Strategies

### 1. Graceful Degradation

The current implementation already provides graceful degradation by falling back to local storage. This is the best approach for production systems.

### 2. Health Checks

Implement periodic health checks for Google Drive connectivity:

```java
// Add a health check method to GoogleDriveService
public boolean isGoogleDriveAvailable() {
    try {
        Drive service = createDriveService();
        // Try a simple operation to test connectivity
        service.about().get().setFields("kind").execute();
        return true;
    } catch (Exception e) {
        logger.warn("Google Drive connectivity test failed: ", e);
        return false;
    }
}
```

### 3. Configuration Validation

Add configuration validation at startup:

```java
// In GoogleDriveService.java
@PostConstruct
public void validateConfiguration() {
    logger.info("Validating Google Drive configuration");
    if (googleDriveEnabled) {
        if (serviceAccountKeyPath != null && !serviceAccountKeyPath.isEmpty()) {
            logger.info("Service account key path configured: {}", serviceAccountKeyPath);
            // Validate service account key file exists
        } else {
            logger.info("Using OAuth2 client credentials");
            if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
                logger.warn("Google Drive OAuth2 credentials not properly configured");
            }
        }
    } else {
        logger.info("Google Drive integration is disabled");
    }
}
```

## Summary

The SocketException is typically a network-level issue that can be resolved by:

1. Checking network connectivity
2. Verifying firewall/proxy settings
3. Ensuring proper SSL/TLS configuration
4. Implementing retry logic for transient errors
5. Providing graceful degradation to local storage

The current implementation already includes many of these best practices, particularly the graceful degradation to local storage when Google Drive operations fail.