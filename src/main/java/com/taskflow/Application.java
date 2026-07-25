package com.taskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application entry point.
 */
@SpringBootApplication(scanBasePackages = "com.taskflow")
public class Application {

    /**
     * Launch the TaskFlow application with all configured beans and profiles.
     *
     * @param args command-line arguments passed to SpringApplication
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
