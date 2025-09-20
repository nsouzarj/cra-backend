package br.adv.cra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;
    private final UserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) 
            throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Set the UserDetailsService in the AuthTokenFilter
        authTokenFilter.setUserDetailsService(userDetailsService);
        
        http.cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(java.util.List.of("*"));
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.setMaxAge(3600L);
                    // Expose headers that might be needed for file operations
                    corsConfiguration.setExposedHeaders(java.util.List.of("Content-Disposition"));
                    return corsConfiguration;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> 
                    auth.requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/api/google-drive/authorize").permitAll()
                        .requestMatchers("/api/google-drive/callback").permitAll()
                        .requestMatchers("/api/google-drive/status").permitAll()
                        .requestMatchers("/api/usuarios/**").hasAnyRole("ADMIN", "ADVOGADO","CORRESPONDENTE")
                        .requestMatchers("/api/correspondentes/**").hasAnyRole("ADMIN", "ADVOGADO", "CORRESPONDENTE")
                        .requestMatchers("/api/processos/**").hasAnyRole("ADMIN", "ADVOGADO", "CORRESPONDENTE")
                        .requestMatchers("/api/solicitacoes/**").hasAnyRole("ADMIN", "ADVOGADO", "CORRESPONDENTE")
                        .requestMatchers("/api/status-solicitacao/**").hasAnyRole("ADMIN", "ADVOGADO", "CORRESPONDENTE")
                        .requestMatchers("/api/tipos-processo/**").hasAnyRole("ADMIN", "ADVOGADO", "CORRESPONDENTE")
                        .requestMatchers("/api/tipos-solicitacao/**").hasAnyRole("ADMIN", "ADVOGADO", "CORRESPONDENTE")
                        .requestMatchers("/api/soli-arquivos/upload").permitAll() // Allow public access to file uploads
                        .requestMatchers("/api/soli-arquivos/*/download").permitAll() // Allow public access to file downloads
                        .requestMatchers("/api/soli-arquivos/**").hasAnyRole("ADMIN", "ADVOGADO", "CORRESPONDENTE") // Restrict other soli-arquivos operations
                        .requestMatchers("/arquivos/**").permitAll() // Allow public access to uploaded files
                        .anyRequest().authenticated()
                );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        // H2 Console Configuration
        http.headers(headers -> headers.frameOptions().sameOrigin());
        
        return http.build();
    }
}