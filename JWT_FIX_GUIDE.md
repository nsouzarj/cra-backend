# JWT Token Fix Guide

## Problem: "JWT strings must contain exactly 2 period characters. Found: 3"

This error indicates that the JWT token structure is malformed or being parsed incorrectly. A valid JWT should have exactly 3 parts separated by 2 periods (header.payload.signature).

## ✅ Fixes Implemented

### 1. Updated JWT Secret Key Handling

**Problem**: The JWT secret was incorrectly configured as Base64, causing parsing issues.

**Solution**: Updated [JwtUtils.java](file://d:\Projetos\cranew\src\main\java\br\adv\cra\security\JwtUtils.java#L119-L144) to properly handle the secret key:
- Detects if secret is Base64 encoded or plain text
- Ensures minimum 256-bit key length for HS256 algorithm
- Robust error handling and fallback mechanisms

### 2. Simplified JWT Secret Configuration

**Updated**: [application.properties](file://d:\Projetos\cranew\src\main\resources\application.properties#L75-L77) with a plain text secret:
```properties
app.jwt.secret=craSecretKeyForJWTTokenGenerationThatNeedsToBeAtLeast256BitsForSecurityPurposes2024
```

### 3. Enhanced Error Logging

**Updated**: [AuthTokenFilter.java](file://d:\Projetos\cranew\src\main\java\br\adv\cra\security\AuthTokenFilter.java#L29-L65) with detailed JWT parsing logs:
- Debug information for token validation
- Better error messages for troubleshooting
- Graceful error handling

### 4. Added Debug Endpoints

**New endpoints** for JWT troubleshooting:
- `POST /api/auth/debug-jwt` - Test JWT generation and parsing
- `POST /api/auth/test-password` - Test password hashing

## 🧪 Testing the JWT Fix

### 1. Debug JWT Generation
```bash
curl -X POST "http://localhost:8080/cra-api/api/auth/debug-jwt" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"username": "admin"}'
```

**Expected Response**:
```json
{
  "username": "admin",
  "tokenGenerated": true,
  "tokenParts": 3,
  "expectedParts": 3,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenLength": 180,
  "header": "eyJhbGciOiJIUzI1NiJ9",
  "payload": "eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY5MzIyODgwMCwiZXhwIjoxNjkzMzE1MjAwfQ",
  "signature": "signature_part",
  "status": "SUCCESS",
  "tokenValid": true,
  "extractedUsername": "admin",
  "usernameMatches": true
}
```

### 2. Test Authentication Flow
```bash
# Login with admin credentials
curl -X POST "http://localhost:8080/cra-api/api/auth/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"login": "admin", "senha": "admin123"}'
```

### 3. Test Protected Endpoint
```bash
# Use the token from login response
curl -X GET "http://localhost:8080/cra-api/api/auth/me" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Accept: application/json"
```

## 🔧 Troubleshooting

### Common Issues and Solutions

1. **Token still malformed**: 
   - Restart the application to pick up new JWT secret
   - Check logs for detailed error messages

2. **"Bad credentials" error**:
   - Ensure password hashes are correctly updated in database
   - Run the password fix script: `database/fix-passwords.sql`

3. **CORS issues**:
   - Verify Insomnia uses correct headers: `Content-Type: application/json`

4. **Token validation fails**:
   - Use the `/debug-jwt` endpoint to verify token generation
   - Check application logs for detailed error information

### Key Files Updated

- ✅ [JwtUtils.java](file://d:\Projetos\cranew\src\main\java\br\adv\cra\security\JwtUtils.java) - Fixed secret key handling
- ✅ [application.properties](file://d:\Projetos\cranew\src\main\resources\application.properties) - Simplified JWT secret
- ✅ [AuthTokenFilter.java](file://d:\Projetos\cranew\src\main\java\br\adv\cra\security\AuthTokenFilter.java) - Enhanced logging
- ✅ [AuthController.java](file://d:\Projetos\cranew\src\main\java\br\adv\cra\controller\AuthController.java) - Added debug endpoint
- ✅ [AuthService.java](file://d:\Projetos\cranew\src\main\java\br\adv\cra\service\AuthService.java) - Added debug method

## 🚀 Next Steps

1. **Start the application**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **Test JWT generation**:
   - Use the `/debug-jwt` endpoint
   - Verify token has exactly 3 parts

3. **Test authentication**:
   - Login with admin/admin123
   - Verify JWT token is returned correctly
   - Test protected endpoints

The JWT parsing error should now be resolved with proper token generation and validation!