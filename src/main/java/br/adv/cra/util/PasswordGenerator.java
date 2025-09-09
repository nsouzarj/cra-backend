package br.adv.cra.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== CRA Backend Password Generator ===");
        System.out.println();
        
        // Generate hashes for all test users
        String adminPassword = "admin123";
        String advogadoPassword = "#Nso1810";
        String correspondentePassword = "crateste";
        String isominaPassword = "isomina123";
        
        String adminHash = encoder.encode(adminPassword);
        String advogadoHash = encoder.encode(advogadoPassword);
        String correspondenteHash = encoder.encode(correspondentePassword);
        String isominaHash = encoder.encode(isominaPassword);
        
        System.out.println("Password: " + adminPassword);
        System.out.println("BCrypt Hash: " + adminHash);
        System.out.println();
        
        System.out.println("Password: " + advogadoPassword);
        System.out.println("BCrypt Hash: " + advogadoHash);
        System.out.println();
        
        System.out.println("Password: " + correspondentePassword);
        System.out.println("BCrypt Hash: " + correspondenteHash);
        System.out.println();
        
        System.out.println("Password: " + isominaPassword);
        System.out.println("BCrypt Hash: " + isominaHash);
        System.out.println();
        
        System.out.println("=== SQL UPDATE STATEMENTS ===");
        System.out.println();
        System.out.println("UPDATE usuario SET senha = '" + adminHash + "' WHERE login = 'admin';");
        System.out.println("UPDATE usuario SET senha = '" + advogadoHash + "' WHERE login = 'advogado1';");
        System.out.println("UPDATE usuario SET senha = '" + correspondenteHash + "' WHERE login = 'corresp1';");
        System.out.println("UPDATE usuario SET senha = '" + isominaHash + "' WHERE login = 'isomina';");
        
        // Verify the hashes work
        System.out.println();
        System.out.println("=== VERIFICATION ===");
        System.out.println("admin123 matches: " + encoder.matches(adminPassword, adminHash));
        System.out.println("adv123 matches: " + encoder.matches(advogadoPassword, advogadoHash));
        System.out.println("corresp123 matches: " + encoder.matches(correspondentePassword, correspondenteHash));
        System.out.println("isomina123 matches: " + encoder.matches(isominaPassword, isominaHash));
    }
}