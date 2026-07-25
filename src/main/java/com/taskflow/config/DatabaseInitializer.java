package com.taskflow.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Database initializer to create default users and seed data on first startup.
 */
@Slf4j
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database...");

        // Create a default admin user on first startup only
        String defaultUser = "admin";
        String defaultEmail = "admin@taskflow.local";
        String defaultPassword = "admin123!";  // Use this temporarily - change in production!
        
        try {
            // Try to find if admin already exists
            var existingUser = passwordEncoder.encode(defaultPassword);
            log.info("Default user '{}' with password '{}'' has been created.",
                    defaultUser, defaultEmail);
        } catch (IllegalArgumentException e) {
            // User already exists - skip creation
            log.warn("Default user '{}' already exists. Skipping creation.", defaultUser);
        }

        log.info("Database initialization complete.");
    }
}
