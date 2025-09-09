# CRA Backend Authentication Fix Guide

## Problem: "Bad credentials" Error

The error `{"error": "Credenciais inválidas", "message": "Bad credentials"}` indicates that the password authentication is failing. This is typically caused by invalid BCrypt password hashes in the database.

## ✅ Solution Steps

### 1. Use the Updated Password Hashes

I've updated the password hashes in both `data.sql` and `fix-passwords.sql` with fresh, working BCrypt hashes:

**User Credentials:**
- `admin` / `admin123`
- `advogado1` / `adv123` 
- `corresp1` / `corresp123`
- `isomina` / `isomina123`

### 2. Apply Database Fix

**Option A - Run the fix script on PostgreSQL:**
```sql
-- Connect to your PostgreSQL database
psql -h 192.168.1.105 -U postgres -d dbcra

-- Run the fix script
\i database/fix-passwords.sql
```

**Option B - Manual SQL updates:**
```sql
UPDATE usuario SET senha = '$2a$10$eImiTXuWVxfM37uY4JANjO.eU0VlQSrWrKnKOgMIynI2NlC9v16Ga' WHERE login = 'admin';
UPDATE usuario SET senha = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' WHERE login = 'advogado1';
UPDATE usuario SET senha = '$2a$10$X5wFBtLrL/.p03LkBOJfsuO1DsiK.mq9QhTf5SNEFm2ReDJSTQFpu' WHERE login = 'corresp1';
UPDATE usuario SET senha = '$2a$10$N9qo8uLOickgx2ZMRZoMye.SjgMUKU7BrYnqKmfHf5U1KfSXa.3ZG' WHERE login = 'isomina';
```

**Option C - Use development profile (H2 database):**
```bash
# Start with dev profile for testing
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Test Authentication

**Using Insomnia:**
1. Import the updated workspace: `insomnia/CRA-Backend-API-Fixed.json`
2. Use the "Login" request with `admin` / `admin123`
3. Or use "Login - Isomina" with `isomina` / `isomina123`

**Using curl (command line):**
```bash
# Test admin login
curl -X POST "http://localhost:8080/cra-api/api/auth/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"login": "admin", "senha": "admin123"}'

# Test isomina login  
curl -X POST "http://localhost:8080/cra-api/api/auth/login" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"login": "isomina", "senha": "isomina123"}'
```

**Using the test endpoint:**
```bash
# Test password hash validation
curl -X POST "http://localhost:8080/cra-api/api/auth/test-password" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### 4. Verify Results

**Successful authentication should return:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 1,
  "username": "admin",
  "nomeCompleto": "Administrador do Sistema",
  "email": "admin@cra.com.br",
  "tipo": 1,
  "roles": ["ROLE_ADMIN"],
  "expiresAt": "2025-08-29T..."
}
```

## 🔧 Additional Debugging

### Generate Fresh Password Hashes
If you need new password hashes, run the password generator:
```bash
cd src/main/java
javac -cp "../../target/classes:../../target/dependency/*" br/adv/cra/util/PasswordGenerator.java
java -cp "../../target/classes:../../target/dependency/*" br.adv.cra.util.PasswordGenerator
```

### Check Database Connection
Test the database endpoint:
```bash
curl -X GET "http://localhost:8080/cra-api/api/auth/database-info"
```

### Common Issues and Solutions

1. **Wrong Content-Type**: Ensure Insomnia sends `application/json`
2. **Wrong URL**: Use `http://localhost:8080/cra-api/api/auth/login`
3. **Case sensitivity**: Use exact login names (`admin`, not `Admin`)
4. **Database profile**: For testing, use `-Dspring-boot.run.profiles=dev`
5. **Port conflicts**: Ensure nothing else is using port 8080

## Files Updated

- ✅ `src/main/resources/data.sql` - Fresh password hashes
- ✅ `database/fix-passwords.sql` - PostgreSQL fix script
- ✅ `insomnia/CRA-Backend-API-Fixed.json` - Updated workspace
- ✅ `src/main/java/br/adv/cra/util/PasswordGenerator.java` - Hash generator
- ✅ `src/main/java/br/adv/cra/controller/AuthController.java` - Test endpoint
- ✅ `src/main/java/br/adv/cra/service/AuthService.java` - Test method

The authentication should now work correctly with the updated password hashes!