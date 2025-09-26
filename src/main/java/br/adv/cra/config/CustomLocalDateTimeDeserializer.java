package br.adv.cra.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomLocalDateTimeDeserializer extends LocalDateTimeDeserializer {
    
    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };
    
    // Pattern for JavaScript Date.toString() format: "Wed Sep 17 2025 00:00:00 GMT-0300 (Brasilia Standard Time)"
    private static final Pattern JS_DATE_PATTERN = Pattern.compile(
        "[A-Za-z]{3}\\s+([A-Za-z]{3})\\s+(\\d{1,2})\\s+(\\d{4})\\s+(\\d{1,2}):(\\d{2}):(\\d{2})\\s+GMT([+-]\\d{4}).*"
    );
    
    // Month names mapping
    private static final String[] MONTH_NAMES = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    
    public CustomLocalDateTimeDeserializer() {
        super(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            // Try to deserialize using the default deserializer first
            LocalDateTime result = super.deserialize(parser, context);
            // If the default deserializer succeeded and returned a non-null value, use it
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            // Continue with custom approach if default fails
        }
        
        // Try our custom approach
        try {
            // Get the text value directly from the parser
            String textValue = parser.getText();
            
            // Handle null case
            if (textValue == null || "null".equals(textValue)) {
                return null;
            }
            
            // Remove quotes if present
            if (textValue.startsWith("\"") && textValue.endsWith("\"")) {
                textValue = textValue.substring(1, textValue.length() - 1);
            }
            
            textValue = textValue.trim();
            
            if (textValue.isEmpty()) {
                return null;
            }
            
            // Try to parse JavaScript Date.toString() format
            LocalDateTime jsDate = parseJavaScriptDateFormat(textValue);
            if (jsDate != null) {
                return jsDate;
            }
            
            // Try each formatter until one works
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    // Check if formatter likely contains time components
                    String formatterString = formatter.toString();
                    boolean hasTime = formatterString.contains("Hour") || formatterString.contains("Minute") || 
                                    formatterString.contains("Second") || formatterString.contains("HH") || 
                                    formatterString.contains("mm") || formatterString.contains("ss");
                    
                    if (hasTime) {
                        // For formatters with time, parse as LocalDateTime directly
                        return LocalDateTime.parse(textValue, formatter);
                    } else {
                        // For date-only formatters, parse as LocalDate and convert to LocalDateTime at start of day
                        LocalDate date = LocalDate.parse(textValue, formatter);
                        return date.atStartOfDay();
                    }
                } catch (DateTimeParseException ex) {
                    // Continue to next formatter
                }
            }
            
            // If we can't parse it, return null instead of throwing an exception
            return null;
        } catch (Exception ex) {
            // If any error occurs, return null
            return null;
        }
    }
    
    /**
     * Parse JavaScript Date.toString() format: "Wed Sep 17 2025 00:00:00 GMT-0300 (Brasilia Standard Time)"
     */
    private LocalDateTime parseJavaScriptDateFormat(String dateString) {
        Matcher matcher = JS_DATE_PATTERN.matcher(dateString);
        if (matcher.matches()) {
            try {
                String monthStr = matcher.group(1);
                int day = Integer.parseInt(matcher.group(2));
                int year = Integer.parseInt(matcher.group(3));
                int hour = Integer.parseInt(matcher.group(4));
                int minute = Integer.parseInt(matcher.group(5));
                int second = Integer.parseInt(matcher.group(6));
                String timezoneOffset = matcher.group(7);
                
                // Convert month name to number
                int month = -1;
                for (int i = 0; i < MONTH_NAMES.length; i++) {
                    if (MONTH_NAMES[i].equalsIgnoreCase(monthStr)) {
                        month = i + 1;
                        break;
                    }
                }
                
                if (month == -1) {
                    return null;
                }
                
                // Handle timezone offset format (GMT-0300 -> -03:00)
                String formattedOffset = timezoneOffset.substring(0, 3) + ":" + timezoneOffset.substring(3);
                
                // Create ZonedDateTime and convert to LocalDateTime in system timezone
                ZoneId gmtZone = ZoneId.of("GMT" + formattedOffset);
                ZonedDateTime zonedDateTime = ZonedDateTime.of(year, month, day, hour, minute, second, 0, gmtZone);
                return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                
            } catch (Exception e) {
                // If parsing fails, continue with other formats
                return null;
            }
        }
        return null;
    }
}