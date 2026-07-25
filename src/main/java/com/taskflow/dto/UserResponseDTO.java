package com.taskflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for User representation in API responses (without sensitive data).
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    /**
     * Unique identifier for the user.
     */
    private Long id;

    /**
     * Username of the user.
     */
    private String username;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * First name of the user.
     */
    private String firstName;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Account status: ACTIVE, INACTIVE, or SUSPENDED.
     */
    private UserStatus status = UserStatus.ACTIVE;

    // Enum for account statuses
    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }
}
