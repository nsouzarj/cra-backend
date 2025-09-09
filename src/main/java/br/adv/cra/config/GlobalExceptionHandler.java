package br.adv.cra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Erro de validação",
                "Os dados fornecidos são inválidos",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                errors
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Erro interno",
                ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro inesperado",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Erro interno do servidor",
                "Ocorreu um erro inesperado",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    public static class ErrorResponse {
        private String error;
        private String message;
        private int status;
        private LocalDateTime timestamp;
        private Map<String, String> details;
        
        public ErrorResponse(String error, String message, int status, LocalDateTime timestamp, Map<String, String> details) {
            this.error = error;
            this.message = message;
            this.status = status;
            this.timestamp = timestamp;
            this.details = details;
        }
        
        // Getters and setters
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Map<String, String> getDetails() { return details; }
        public void setDetails(Map<String, String> details) { this.details = details; }
    }
}