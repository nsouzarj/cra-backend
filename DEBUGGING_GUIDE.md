# Debugging Guide for CRA Backend Application

## Problem Description

When debugging the CRA Backend application, you may encounter the following error:

```
java.lang.IllegalArgumentException: null KeyStore name
```

This error occurs when the JVM tries to initialize SSL/TLS context but cannot find a properly configured keystore.

## Root Cause

This issue typically happens when:
1. SSL is enabled but no keystore is properly configured
2. The JVM is trying to initialize SSL context with incomplete configuration
3. Debug configurations are not properly set up to disable SSL

## Solution

### 1. Use the Correct Debug Configuration

Instead of using the default debug configuration, use one of the specific configurations we've created:

1. **CraBackendApplication (Dev Profile)** - Recommended for debugging
2. **CraBackendApplication (No SSL)** - Alternative option
3. **CraBackendApplication (Debug)** - For advanced debugging scenarios

### 2. How to Use the Debug Configurations

1. Open the Debug view in VS Code/Qoder IDE (Ctrl+Shift+D)
2. Select one of the configurations mentioned above from the dropdown
3. Click the green "Start Debugging" button (F5) or "Run Without Debugging" (Ctrl+F5)

### 3. Configuration Details

#### CraBackendApplication (Dev Profile)
- Activates the `dev` Spring profile
- Explicitly disables SSL with system properties
- Sets the correct TLS protocol version

#### CraBackendApplication (No SSL)
- Explicitly disables SSL with system properties
- Sets the correct TLS protocol version

#### CraBackendApplication (Debug)
- Activates the `dev` Spring profile
- Explicitly disables SSL with system properties
- Sets the correct TLS protocol version
- Allows security manager for debugging purposes

### 4. Manual Command Line Debugging

If you prefer to run from the command line with debugging enabled:

```bash
# For development profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Djavax.net.ssl.trustStore= -Djavax.net.ssl.keyStore= -Djdk.tls.client.protocols=TLSv1.2"

# With debug agent
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
     -Dspring.profiles.active=dev \
     -Djavax.net.ssl.trustStore= \
     -Djavax.net.ssl.keyStore= \
     -Djdk.tls.client.protocols=TLSv1.2 \
     -jar target/cra-backend-0.0.1-SNAPSHOT.jar
```

### 5. Troubleshooting

#### If the error persists:

1. **Check your IDE configuration**:
   - Make sure you're using one of the specific debug configurations
   - Verify that the configuration has the correct VM arguments

2. **Verify application properties**:
   - Check that `server.ssl.enabled=false` is set in your active profile
   - Ensure `server.ssl.key-store=` and `server.ssl.trust-store=` are empty

3. **Check for environment variables**:
   - Make sure no environment variables are enabling SSL
   - Look for `JAVA_OPTS` or similar variables that might contain SSL settings

4. **Clean and rebuild**:
   - Run `mvn clean package` to ensure all configurations are properly applied
   - Restart your IDE to clear any cached configurations

#### If you're still having issues:

1. Try running with the "CraBackendApplication (Debug)" configuration
2. Check the terminal output to verify that the correct VM arguments are being passed
3. If necessary, create a new debug configuration by copying one of the existing ones and modifying as needed

### 6. Important Notes

- Never use these debug configurations in production
- The SSL disabling is only for development and debugging purposes
- For production deployments, properly configure SSL with valid certificates
- If you need to test SSL functionality, do so in a separate, properly configured environment