# Maven Compilation Issue Resolution

## Problem Description

The Maven build process is hanging during the compilation phase, specifically when trying to compile the GoogleDriveService.java file. This issue is related to the Google API client dependencies and the GoogleNetHttpTransport.newTrustedTransport() call.

## Root Cause Analysis

The issue occurs due to several factors:

1. **Network Connectivity**: Maven may be having trouble downloading dependencies from remote repositories
2. **Google API Client Dependencies**: The Google API client libraries may not be properly resolved
3. **HTTP Transport Creation**: The GoogleNetHttpTransport.newTrustedTransport() call can hang if there are network issues

## Immediate Solutions

### 1. Check Network Connectivity

Ensure your machine has internet access and can reach Maven repositories:

```bash
# Test connectivity to Maven Central
ping repo.maven.apache.org

# Test connectivity to Google APIs
ping www.googleapis.com
```

### 2. Clear Maven Cache

Sometimes corrupted dependencies can cause issues:

```bash
# Delete the local Maven repository cache
rm -rf ~/.m2/repository/com/google

# Or on Windows
rmdir /s %USERPROFILE%\.m2\repository\com\google
```

### 3. Force Update Dependencies

Try to force Maven to update dependencies:

```bash
cd d:\Projetos\craweb\cra-backend
mvn clean compile -U
```

### 4. Check Maven Settings

Verify your Maven settings.xml file doesn't have any proxy issues:

```xml
<!-- In ~/.m2/settings.xml -->
<settings>
  <proxies>
    <!-- Add proxy configuration if needed -->
    <proxy>
      <id>optional</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>proxy.host</host>
      <port>8080</port>
      <username>proxyuser</username>
      <password>proxypass</password>
      <nonProxyHosts>local.net|some.host.com</nonProxyHosts>
    </proxy>
  </proxies>
</settings>
```

## Long-term Solutions

### 1. Update Google API Dependencies

Consider updating to newer versions of the Google API client libraries in your pom.xml:

```xml
<!-- Google Drive API Client -->
<dependency>
    <groupId>com.google.api-client</groupId>
    <artifactId>google-api-client</artifactId>
    <version>2.2.0</version>
</dependency>

<dependency>
    <groupId>com.google.oauth-client</groupId>
    <artifactId>google-oauth-client-jetty</artifactId>
    <version>1.34.1</version>
</dependency>

<dependency>
    <groupId>com.google.apis</groupId>
    <artifactId>google-api-services-drive</artifactId>
    <version>v3-rev20230523-2.0.0</version>
</dependency>
```

### 2. Alternative HTTP Transport

Instead of using GoogleNetHttpTransport.newTrustedTransport(), you can use a simpler HTTP transport:

```java
// In GoogleDriveService.java, replace:
final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

// With:
final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder().build();
```

### 3. Add Connection Timeouts

Add connection timeouts to prevent hanging:

```java
// In GoogleDriveService.java
private Drive createDriveService() throws GeneralSecurityException, IOException {
    // Create HTTP transport with timeout
    final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport.Builder()
        .setConnectionFactory(new DefaultConnectionFactory() {
            @Override
            public HttpURLConnection openConnection(URL url) throws IOException {
                HttpURLConnection connection = super.openConnection(url);
                connection.setConnectTimeout(30000); // 30 seconds
                connection.setReadTimeout(30000);    // 30 seconds
                return connection;
            }
        })
        .build();
    
    // Rest of the method...
}
```

## Workaround Solutions

### 1. Disable Google Drive Integration Temporarily

If you need to compile and run the application without Google Drive integration:

1. Set `google.drive.oauth.enabled=false` in application.properties
2. Comment out or remove the GoogleDriveService bean from SoliArquivoService
3. Ensure all Google Drive related code paths are bypassed

### 2. Use Local Storage Only

Modify the SoliArquivoService to always use local storage:

```java
// In SoliArquivoService.java, modify the salvarAnexo method:
public SoliArquivo salvarAnexo(MultipartFile file, Long solicitacaoId, String origem, String storageLocation) throws IOException {
    logger.info("Saving attachment for solicitacao ID: {}", solicitacaoId);
    logger.info("Origin: {}, Storage location: {}, File name: {}, File size: {} bytes", 
               origem, storageLocation, file.getOriginalFilename(), file.getSize());

    Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
            .orElseThrow(() -> {
                logger.error("Solicitação with ID {} not found", solicitacaoId);
                return new RuntimeException("Solicitação não encontrada");
            });
    
    // Always save locally regardless of configuration
    logger.info("Saving file locally (Google Drive disabled)");
    return saveFileLocally(file, solicitacao, origem);
}
```

## Testing the Solution

### 1. Verify Dependencies

Check that all dependencies are properly resolved:

```bash
mvn dependency:tree | grep google
```

### 2. Compile Individual Classes

Try compiling individual classes to isolate the issue:

```bash
javac -cp "target/classes;~/.m2/repository/com/google/api-client/google-api-client/2.0.0/google-api-client-2.0.0.jar;~/.m2/repository/com/google/http-client/google-http-client/1.42.1/google-http-client-1.42.1.jar;~/.m2/repository/com/google/http-client/google-http-client-gson/1.42.1/google-http-client-gson-1.42.1.jar;~/.m2/repository/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar;~/.m2/repository/com/google/apis/google-api-services-drive/v3-rev20220815-2.0.0/google-api-services-drive-v3-rev20220815-2.0.0.jar" src/main/java/br/adv/cra/service/GoogleDriveService.java
```

### 3. Run with Debug Information

Run Maven with debug information to see where it's hanging:

```bash
mvn compile -X
```

## Summary

The Maven compilation issue is likely caused by network connectivity problems when resolving Google API dependencies or issues with the HTTP transport creation. The solutions include:

1. Checking network connectivity
2. Clearing Maven cache
3. Updating Google API dependencies
4. Using alternative HTTP transport implementations
5. Adding proper timeouts
6. Disabling Google Drive integration temporarily if needed

These steps should resolve the compilation hanging issue and allow the application to build successfully.