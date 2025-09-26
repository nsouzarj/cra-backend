package br.adv.cra.config;

import br.adv.cra.entity.Solicitacao;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

@Configuration
public class JacksonConfig {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule for basic JSR310 support
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapper.registerModule(javaTimeModule);
        
        // Register our custom module with the custom serializer and deserializer
        SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        customModule.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());
        mapper.registerModule(customModule);
        
        // Add mixin for Solicitacao to handle relationships properly
        mapper.addMixIn(Solicitacao.class, SolicitacaoMixin.class);
        
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        
        // Set timezone to system default
        mapper.setTimeZone(java.util.TimeZone.getDefault());
        
        return mapper;
    }
}