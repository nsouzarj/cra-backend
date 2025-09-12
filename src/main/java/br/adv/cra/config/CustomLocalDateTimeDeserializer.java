package br.adv.cra.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateTimeDeserializer extends LocalDateTimeDeserializer {
    
    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };
    
    public CustomLocalDateTimeDeserializer() {
        super(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        
        // Handle array format [year, month, day, hour, minute, second, nanosecond]
        if (node.isArray() && node.size() >= 3) {
            int year = node.get(0).asInt();
            int month = node.get(1).asInt();
            int day = node.get(2).asInt();
            int hour = node.size() > 3 ? node.get(3).asInt() : 0;
            int minute = node.size() > 4 ? node.get(4).asInt() : 0;
            int second = node.size() > 5 ? node.get(5).asInt() : 0;
            // int nanosecond = node.size() > 6 ? node.get(6).asInt() : 0;
            
            return LocalDateTime.of(year, month, day, hour, minute, second);
        }
        
        // Handle string format
        String dateString = node.asText().trim();
        
        if (dateString.isEmpty()) {
            return null;
        }
        
        // Try each formatter until one works
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                if (formatter.toString().contains("HH") || formatter.toString().contains("HH:mm")) {
                    // For formatters with time, parse as LocalDateTime directly
                    return LocalDateTime.parse(dateString, formatter);
                } else {
                    // For date-only formatters, parse as LocalDate and convert to LocalDateTime at start of day
                    LocalDate date = LocalDate.parse(dateString, formatter);
                    return date.atStartOfDay();
                }
            } catch (DateTimeParseException e) {
                // Continue to next formatter
            }
        }
        
        // If none of the formatters worked, throw an exception
        throw new IOException("Unable to parse date: " + dateString);
    }
}