package com.taskflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User entity representing an authenticated user in the system.
 */
@Entity
@Table(name = "users")
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
public class User implements Serializable {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username of the user.
     */
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Encrypted password hash for the user.
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * Email address of the user.
     */
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    /**
     * First name of the user.
     */
    @Column(nullable = false, length = 50)
    private String firstName;

    /**
     * Last name of the user.
     */
    @Column(nullable = false, length = 50)
    private String lastName;

    /**
     * Account status: ACTIVE or INACTIVE.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Timestamp when the user was created in the system.
     */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update to the user record.
     */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    /**
     * Enum representing possible account statuses.
     */
    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }

    // Constructors for JPA
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
