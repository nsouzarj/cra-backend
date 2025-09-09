package br.adv.cra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    // CORS configuration is handled in SecurityConfig
    // to avoid conflicts with Spring Security
    
}