package br.adv.cra.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DebugTest {

    @Test
    void testDeserializeJavaScriptDateFormat() throws Exception {
        // Arrange
        CustomLocalDateTimeDeserializer deserializer = new CustomLocalDateTimeDeserializer();
        
        // JavaScript Date.toString() format - this is what we get from the test
        String jsDateString = "Wed Sep 17 2025 00:00:00 GMT-0300 (Brasilia Standard Time)";
        System.out.println("Input date string: '" + jsDateString + "'");
        
        // Create a JsonParser with the string value (not quoted)
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser("\"" + jsDateString + "\"");
        parser.nextToken(); // Move to the value
        
        System.out.println("Parser current token: " + parser.getCurrentToken());
        System.out.println("Parser text: '" + parser.getText() + "'");
        
        DeserializationContext context = Mockito.mock(DeserializationContext.class);
        
        // Act
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        // Assert
        System.out.println("Result: " + result);
        assertNotNull(result, "Deserialized date should not be null");
    }
    
    @Test
    void testRegexPattern() {
        String jsDateString = "Wed Sep 17 2025 00:00:00 GMT-0300 (Brasilia Standard Time)";
        System.out.println("Testing regex pattern on: '" + jsDateString + "'");
        
        // Test our regex pattern directly
        java.util.regex.Pattern JS_DATE_PATTERN = java.util.regex.Pattern.compile(
            "[A-Za-z]{3}\\s+([A-Za-z]{3})\\s+(\\d{1,2})\\s+(\\d{4})\\s+(\\d{1,2}):(\\d{2}):(\\d{2})\\s+GMT([+-]\\d{4}).*"
        );
        
        java.util.regex.Matcher matcher = JS_DATE_PATTERN.matcher(jsDateString);
        boolean matches = matcher.matches();
        System.out.println("Pattern matches: " + matches);
        
        if (matches) {
            System.out.println("Groups:");
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println("  Group " + i + ": '" + matcher.group(i) + "'");
            }
        }
    }
}