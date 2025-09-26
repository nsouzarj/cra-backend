package br.adv.cra.config;

import org.junit.jupiter.api.Test;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class FormatterToStringTest {

    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss") // Add this format for your specific case
    };

    @Test
    void testFormatterToString() {
        for (DateTimeFormatter formatter : FORMATTERS) {
            System.out.println("Formatter: " + formatter);
            System.out.println("toString(): " + formatter.toString());
            System.out.println("Contains HH: " + formatter.toString().contains("HH"));
            System.out.println("Contains HH:mm: " + formatter.toString().contains("HH:mm"));
            System.out.println("Condition result: " + (formatter.toString().contains("HH") || formatter.toString().contains("HH:mm")));
            System.out.println("---");
        }
    }
}