# Persistent SocketException Troubleshooting Guide

## Problem Description

Despite implementing retry logic and error handling, you're still encountering:
```
java.net.SocketException: An established connection was aborted by the software in your host machine
```

This error occurs during Google Drive file uploads and indicates a network-level issue during the SSL/TLS handshake process.

## Root Cause Analysis

The SocketException is happening at the network/transport layer, specifically during the SSL handshake when establishing a connection to Google's servers. This suggests:

1. **Network connectivity issues** between your server and Google's APIs
2. **Firewall/proxy interference** blocking or terminating the connection
3. **SSL/TLS configuration problems** in your environment
4. **Resource limitations** causing connection timeouts
5. **Antivirus/security software** interfering with the connection

## Immediate Solutions

### 1. Network Diagnostics

First, verify basic connectivity to Google's services:

```bash
# Test DNS resolution
nslookup www.googleapis.com

# Test basic connectivity
ping www.googleapis.com

# Test HTTPS connectivity (requires telnet client)
telnet www.googleapis.com 443

# Test with PowerShell (Windows)
Test-NetConnection www.googleapis.com -Port 443
```

### 2. Firewall and Security Software

Check if any security software is interfering:

1. **Temporarily disable firewall/antivirus** to test if they're causing the issue
2. **Add exceptions** for your Java application or the specific ports used
3. **Check proxy settings** if you're behind a corporate firewall

### 3. Java SSL Configuration

Update your Java SSL configuration:

```bash
# Add these JVM arguments when running your application
-Djavax.net.debug=ssl,handshake
-Dhttps.protocols=TLSv1.2,TLSv1.3
-Djavax.net.ssl.trustStore=NUL
-Djavax.net.ssl.trustStoreType=Windows-ROOT
```

### 4. Proxy Configuration

If you're behind a proxy, configure it properly:

```java
// Add to your application startup or GoogleDriveService
System.setProperty("https.proxyHost", "your.proxy.host");
System.setProperty("https.proxyPort", "proxy_port");
// If authentication is required:
System.setProperty("https.proxyUser", "username");
System.setProperty("https.proxyPassword", "password");
```

## Advanced Solutions

### 1. Connection Pooling and Timeouts

Enhance the HTTP transport configuration with better connection management:

```java
// In GoogleDriveService.createDriveService()
final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder()
    .setConnectionFactory(new DefaultConnectionFactory() {
        @Override
        public HttpURLConnection openConnection(URL url) throws IOException {
            HttpURLConnection connection = super.openConnection(url);
            connection.setConnectTimeout(30000); // 30 seconds
            connection.setReadTimeout(30000);    // 30 seconds
            connection.setRequestProperty("Connection", "keep-alive");
            return connection;
        }
    })
    .build();
```

### 2. JVM Network Settings

Add these JVM arguments to improve network stability:

```bash
-Dsun.net.client.defaultConnectTimeout=30000
-Dsun.net.client.defaultReadTimeout=30000
-Djava.net.preferIPv4Stack=true
-Djdk.tls.client.protocols=TLSv1.2,TLSv1.3
```

### 3. Google API Client Configuration

Configure the Google API client with more robust settings:

```java
// In GoogleDriveService.createDriveService()
return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getEmptyCredential())
    .setApplicationName(APPLICATION_NAME)
    .setHttpRequestInitializer(new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest request) throws IOException {
            getEmptyCredential().initialize(request);
            request.setConnectTimeout(30000);  // 30 seconds
            request.setReadTimeout(30000);     // 30 seconds
            request.setNumberOfRetries(3);     // Built-in retries
        }
    })
    .build();
```

## Alternative Approaches

### 1. Disable Google Drive Temporarily

If the issue persists, disable Google Drive integration temporarily:

In [application.properties](src/main/resources/application.properties):
```properties
google.drive.oauth.enabled=false
```

This will force all file operations to use local storage.

### 2. Implement a Health Check

Add a health check endpoint to monitor Google Drive connectivity:

```java
// In GoogleDriveService.java
public boolean isGoogleDriveHealthy() {
    try {
        // Try a simple operation to test connectivity
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder().build();
        // Perform a lightweight test
        return true;
    } catch (Exception e) {
        logger.warn("Google Drive health check failed: ", e);
        return false;
    }
}
```

### 3. Use a Different Network Interface

If you have multiple network interfaces, try specifying which one to use:

```bash
# JVM argument to prefer a specific network interface
-Djava.net.preferIPv4Stack=true
-Djava.net.preferIPv6Addresses=false
```

## Environment-Specific Solutions

### Windows-Specific Fixes

Since you're using Windows 24H2:

1. **Reset network settings**:
   ```cmd
   netsh winsock reset
   netsh int ip reset
   ```

2. **Flush DNS cache**:
   ```cmd
   ipconfig /flushdns
   ```

3. **Renew IP configuration**:
   ```cmd
   ipconfig /renew
   ```

### Java Version Compatibility

Ensure you're using a compatible Java version:

1. **Update to the latest Java 23 patch**:
   ```bash
   java -version
   ```

2. **Consider using Java 17 or 21** if issues persist with Java 23

## Monitoring and Logging

### Enhanced Logging

Add more detailed logging to identify where exactly the failure occurs:

```java
// In GoogleDriveService.java
logger.info("Starting Google Drive connection attempt to: {}", "www.googleapis.com");
// Add timing information
long startTime = System.currentTimeMillis();
try {
    // Google Drive operations
    long endTime = System.currentTimeMillis();
    logger.info("Google Drive operation completed in {} ms", (endTime - startTime));
} catch (Exception e) {
    long endTime = System.currentTimeMillis();
    logger.error("Google Drive operation failed after {} ms", (endTime - startTime), e);
    throw e;
}
```

### Network Monitoring

Use network monitoring tools to capture the exact point of failure:

1. **Wireshark** to capture network traffic
2. **Windows Resource Monitor** to check network activity
3. **Process Monitor** to check file/registry access

## Testing Strategy

### 1. Isolated Testing

Create a simple test to isolate the Google Drive connection issue:

```java
// Create a simple test class
public class GoogleDriveConnectionTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Google Drive connection...");
            NetHttpTransport transport = new NetHttpTransport.Builder().build();
            System.out.println("HTTP Transport created successfully");
            
            // Try to make a simple HTTPS request
            URL url = new URL("https://www.googleapis.com/drive/v3/about?fields=kind");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 2. Gradual Rollout

If you must use Google Drive:

1. **Start with a small subset of files** to test
2. **Monitor closely** for any failures
3. **Gradually increase** the load
4. **Have a rollback plan** to disable Google Drive if issues persist

## Summary

The persistent SocketException indicates a fundamental network connectivity issue between your server and Google's APIs. The solutions involve:

1. **Network diagnostics** to identify connectivity problems
2. **Firewall/security software adjustments** to allow connections
3. **Java SSL configuration** improvements
4. **Enhanced connection management** with proper timeouts
5. **Fallback mechanisms** to local storage when Google Drive fails

The most immediate solution is to temporarily disable Google Drive integration and rely on local storage while you work through the network issues. Once connectivity is resolved, you can re-enable Google Drive integration with the enhanced error handling we've implemented.