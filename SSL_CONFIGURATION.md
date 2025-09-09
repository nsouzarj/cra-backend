# SSL Configuration and KeyStore Issue Resolution

## Problem Description

When running the application in debug mode, you may encounter the following errors:

1. `java.lang.IllegalArgumentException: null KeyStore name`
2. `java.security.AccessControlException: access denied ("java.lang.RuntimePermission" "getClassLoader")`

These errors occur when the JVM tries to access security-related resources but lacks proper permissions or configuration.

## Root Cause

1. **KeyStore Error**: This occurs when the JVM tries to initialize SSL/TLS context but cannot find a properly configured keystore.
2. **AccessControlException**: This happens when Java Security Manager restricts access to certain operations like getting the class loader, which is common in development environments.

## Solution

### 1. Application Configuration

The application has been configured to disable SSL for development and testing environments:

- In `application.properties`: `server.ssl.enabled=false`
- In `application-dev.properties`: `server.ssl.enabled=false`
- In `application-test.properties`: `server.ssl.enabled=false`

For production, SSL can be enabled with proper configuration in `application-prod.properties`.

### 2. IDE Debug Configuration

The `.vscode/launch.json` file has been updated with specific configurations:

1. **Standard Configuration**: Uses default SSL settings
2. **No SSL Configuration**: Explicitly disables SSL with JVM arguments:
   ```json
   "vmArgs": [
       "-Djavax.net.ssl.trustStore=",
       "-Djavax.net.ssl.keyStore=",
       "-Djdk.tls.client.protocols=TLSv1.2"
   ]
   ```
3. **Debug Configuration with Security Policy**: Disables SSL and provides necessary security permissions:
   ```json
   "vmArgs": [
       "-Djavax.net.ssl.trustStore=",
       "-Djavax.net.ssl.keyStore=",
       "-Djdk.tls.client.protocols=TLSv1.2",
       "-Djava.security.manager=allow",
       "-Djava.security.policy==${workspaceFolder}/security.policy"
   ]
   ```

### 3. Security Policy File

A security policy file (`security.policy`) has been created to grant necessary permissions for development:

- `RuntimePermission "getClassLoader"` - Required for accessing class loaders
- `RuntimePermission "setContextClassLoader"` - Required for setting context class loaders
- Other permissions needed for database access, file I/O, and network operations

### 4. Running the Application

#### For Development:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### For Debugging:
Use the "CraBackendApplication (Debug)" configuration in VS Code/Qoder IDE.

#### For Production:
Ensure you have a valid keystore file and configure the following properties in `application-prod.properties`:
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your-keystore-password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=your-key-alias
```

## Creating a Keystore (For Production)

If you need to enable SSL in production, create a keystore with:

```bash
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
```

Then place the keystore file in `src/main/resources` and update the application-prod.properties accordingly.

## Troubleshooting

If you still encounter issues:

1. Check if any environment variables are setting SSL properties
2. Verify no JVM arguments are being passed that conflict with SSL configuration
3. Ensure your IDE debug configuration is using the correct profile
4. Try running with the "CraBackendApplication (Debug)" configuration which includes the security policy
5. If problems persist, temporarily disable the Security Manager by removing `-Djava.security.manager=allow` from VM arguments

## Additional Notes

- The security policy is only recommended for development environments
- Never use the permissive security policy in production
- For production deployments, configure proper security policies according to your security requirements