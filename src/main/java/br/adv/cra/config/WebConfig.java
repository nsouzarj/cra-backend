package br.adv.cra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Autowired
    private RequestLoggingFilter requestLoggingFilter;
    
    // CORS configuration is handled in SecurityConfig
    // to avoid conflicts with Spring Security
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from the upload directory
        // Convert Windows path separators to URL format
        String location = "file:///" + uploadDir.replace("\\", "/") + "/";
        registry.addResourceHandler("/arquivos/**")
                .addResourceLocations(location);
    }
    
    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> loggingFilter() {
        FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(requestLoggingFilter);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}