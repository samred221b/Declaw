package com.taskflow.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Simple security configuration — no JWT, just HTTP auth and BCrypt.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    /**
     * Allow all endpoints. Use simple HTTP Basic auth if needed via browser headers.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF: enable for stateful sessions; we'll skip it if you want truly REST-only
        http.csrf().disable();

        // Session management: stateless by default (no HTTP session cookies)
        http.sessionManagement()
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS);

        // No authentication required — every endpoint is public.
        // If you later need basic auth, uncomment the line below and add a filter:
        // http.authorizeHttpRequests().anyRequest().authenticated();

        return http.build();
    }

    /**
     * BCrypt password encoder for user registration (stored in DB but no JWT).  
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
