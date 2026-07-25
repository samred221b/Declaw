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
     * Using fully qualified entity reference to avoid ambiguity with DTO's local UserStatus enum.
     */
    private com.taskflow.entity.User.UserStatus status = com.taskflow.entity.User.UserStatus.ACTIVE;

    // Note: The local UserStatus enum has been removed. Use only the entity.User.UserStatus type.
}
