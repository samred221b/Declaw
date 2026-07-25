package com.taskflow.config;

import com.taskflow.entity.User;
import com.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Database initializer to create default users and seed data on first startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database...");

        String defaultUsername = "admin";
        String defaultEmail = "admin@taskflow.local";
        String defaultPassword = "admin123!";  // Change in production!

        if (!userRepository.existsByUsername(defaultUsername)) {
            User admin = User.builder()
                    .username(defaultUsername)
                    .email(defaultEmail)
                    .firstName("Admin")
                    .lastName("User")
                    .passwordHash(passwordEncoder.encode(defaultPassword))
                    .status(User.UserStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
            log.info("Default admin user '{}' created.", defaultUsername);
        } else {
            log.info("Default admin user '{}' already exists. Skipping.", defaultUsername);
        }

        log.info("Database initialization complete.");
    }
}
