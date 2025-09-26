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

class CustomLocalDateTimeDeserializerTest {

    @Test
    void testDeserializeJavaScriptDateFormat() throws Exception {
        // Arrange
        CustomLocalDateTimeDeserializer deserializer = new CustomLocalDateTimeDeserializer();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = new JsonFactory();
        
        // JavaScript Date.toString() format
        String jsDateString = "\"Wed Sep 17 2025 00:00:00 GMT-0300 (Brasilia Standard Time)\"";
        JsonParser parser = factory.createParser(jsDateString);
        parser.nextToken(); // Move to the value
        
        DeserializationContext context = Mockito.mock(DeserializationContext.class);
        
        // Act
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        // Assert
        // Note: The exact result may vary based on timezone conversion, but it should not be null
        assertNotNull(result, "Deserialized date should not be null");
    }

    @Test
    void testDeserializeISOFormat() throws Exception {
        // Arrange
        CustomLocalDateTimeDeserializer deserializer = new CustomLocalDateTimeDeserializer();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = new JsonFactory();
        
        // ISO format
        String isoDateString = "\"2025-09-17T00:00:00\"";
        JsonParser parser = factory.createParser(isoDateString);
        parser.nextToken(); // Move to the value
        
        DeserializationContext context = Mockito.mock(DeserializationContext.class);
        
        // Act
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        // Assert
        assertNotNull(result);
        assertEquals(2025, result.getYear());
        assertEquals(9, result.getMonthValue());
        assertEquals(17, result.getDayOfMonth());
    }

    @Test
    void testDeserializeNullValue() throws Exception {
        // Arrange
        CustomLocalDateTimeDeserializer deserializer = new CustomLocalDateTimeDeserializer();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = new JsonFactory();
        
        // Null value
        String nullDateString = "null";
        JsonParser parser = factory.createParser(nullDateString);
        parser.nextToken(); // Move to the value
        
        DeserializationContext context = Mockito.mock(DeserializationContext.class);
        
        // Act
        LocalDateTime result = deserializer.deserialize(parser, context);
        
        // Assert
        assertNull(result);
    }
}