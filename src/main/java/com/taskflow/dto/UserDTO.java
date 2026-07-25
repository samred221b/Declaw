package com.taskflow.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for User creation and update operations.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * Username of the user.
     */
    private String username;

    /**
     * Email address of the user.
     *
     * @Email ensures valid email format and non-empty value
     */
    @Email(message = "Invalid email format")
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
     * Plain text password for registration (will be hashed on save).
     */
    private String password;

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
