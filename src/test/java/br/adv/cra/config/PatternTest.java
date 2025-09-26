package br.adv.cra.config;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class PatternTest {

    @Test
    void testBrazilianPattern() {
        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String dateString = "16/09/2025 00:00:00";
        
        System.out.println("Pattern: " + pattern);
        System.out.println("Formatter: " + formatter);
        System.out.println("Date string: " + dateString);
        
        try {
            LocalDateTime result = LocalDateTime.parse(dateString, formatter);
            System.out.println("Parsed result: " + result);
            assertNotNull(result);
            assertEquals(2025, result.getYear());
            assertEquals(9, result.getMonthValue());
            assertEquals(16, result.getDayOfMonth());
        } catch (DateTimeParseException e) {
            System.out.println("Parse error: " + e.getMessage());
            e.printStackTrace();
            fail("Failed to parse with pattern: " + pattern);
        }
    }
}