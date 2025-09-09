import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordHashes {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Test the hashes from data.sql
        String adminHash = "$2a$10$eImiTXuWVxfM37uY4JANjO.eU0VlQSrWrKnKOgMIynI2NlC9v16Ga";
        String advogadoHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        String correspHash = "$2a$10$X5wFBtLrL/.p03LkBOJfsuO1DsiK.mq9QhTf5SNEFm2ReDJSTQFpu";
        String isominaHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye.SjgMUKU7BrYnqKmfHf5U1KfSXa.3ZG";
        
        System.out.println("=== Password Hash Validation Test ===");
        System.out.println();
        
        // Test admin credentials
        boolean adminValid = encoder.matches("admin123", adminHash);
        System.out.println("admin / admin123: " + (adminValid ? "✓ VALID" : "✗ INVALID"));
        System.out.println("Hash: " + adminHash);
        System.out.println();
        
        // Test advogado credentials
        boolean advValid = encoder.matches("adv123", advogadoHash);
        System.out.println("advogado1 / adv123: " + (advValid ? "✓ VALID" : "✗ INVALID"));
        System.out.println("Hash: " + advogadoHash);
        System.out.println();
        
        // Test correspondente credentials
        boolean correspValid = encoder.matches("corresp123", correspHash);
        System.out.println("corresp1 / corresp123: " + (correspValid ? "✓ VALID" : "✗ INVALID"));
        System.out.println("Hash: " + correspHash);
        System.out.println();
        
        // Test isomina credentials
        boolean isominaValid = encoder.matches("isomina123", isominaHash);
        System.out.println("isomina / isomina123: " + (isominaValid ? "✓ VALID" : "✗ INVALID"));
        System.out.println("Hash: " + isominaHash);
        System.out.println();
        
        // Generate fresh hashes if needed
        System.out.println("=== Fresh Hash Generation ===");
        System.out.println("admin123 -> " + encoder.encode("admin123"));
        System.out.println("isomina123 -> " + encoder.encode("isomina123"));
    }
}