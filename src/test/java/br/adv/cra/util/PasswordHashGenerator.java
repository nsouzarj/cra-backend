package br.adv.cra.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for testing purposes
 * Run this class to generate hashes for passwords
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // Generate hashes for the default passwords
        System.out.println("=== BCrypt Password Hashes ===");
        System.out.println();
        
        String[] passwords = {"admin123", "adv123", "corresp123", "isomina123"};
        String[] users = {"admin", "advogado1", "corresp1", "isomina"};
        
        for (int i = 0; i < passwords.length; i++) {
            String hash = passwordEncoder.encode(passwords[i]);
            System.out.println(users[i] + "/" + passwords[i] + " -> " + hash);
        }
        
        System.out.println();
        System.out.println("=== SQL UPDATE Statements ===");
        for (int i = 0; i < passwords.length; i++) {
            String hash = passwordEncoder.encode(passwords[i]);
            System.out.println("UPDATE usuario SET senha = '" + hash + "' WHERE login = '" + users[i] + "';");
        }
        
        System.out.println();
        System.out.println("=== Test Custom Password ===");
        // Test with a custom password
        String customPassword = "test123";
        String customHash = passwordEncoder.encode(customPassword);
        System.out.println("Custom password '" + customPassword + "' -> " + customHash);
        
        // Verify the hash works
        boolean matches = passwordEncoder.matches(customPassword, customHash);
        System.out.println("Verification: " + matches + " ✓");
    }
}