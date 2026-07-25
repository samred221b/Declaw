package com.taskflow.service;

import com.taskflow.dto.*;
import com.taskflow.entity.User;
import com.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Service layer for user management operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user with the system.
     *
     * @param dto the UserDTO containing registration data
     * @return the created UserResponseDTO without sensitive information
     */
    public UserResponseDTO register(UserDTO dto) {
        // Validate input
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + dto.getUsername());
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }

        // Hash the password before storing it in the database
        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        // Create and persist the user with default values
        User newUser = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .passwordHash(hashedPassword)
                .status(dto.getStatus() != null ? dto.getStatus() : User.UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save the new user entity to persistence
        userRepository.save(newUser);

        return convertToResponseDTO(newUser);
    }

    /**
     * Retrieve a user by their unique identifier.
     *
     * @param id the user ID to look up
     * @return UserResponseDTO for the found user, or null if not found
     */
    public UserResponseDTO getById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElse(null);
    }

    /**
     * Retrieve a user by their username.
     *
     * @param username the unique username to look up
     * @return UserResponseDTO for the found user, or null if not found
     */
    public UserResponseDTO getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToResponseDTO)
                .orElse(null);
    }

    /**
     * Retrieve a user by their email address.
     *
     * @param email the unique email to look up
     * @return UserResponseDTO for the found user, or null if not found
     */
    public UserResponseDTO getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToResponseDTO)
                .orElse(null);
    }

    /**
     * Update an existing user's profile information.
     *
     * @param id the user ID to update
     * @param dto the UserDTO containing updated fields
     * @return the updated UserResponseDTO
     */
    public UserResponseDTO update(Long id, UserDTO dto) {
        // Validate input
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            userRepository.findByEmail(dto.getEmail())
                    .ifPresent(e -> { throw new IllegalArgumentException("Email already in use: " + dto.getEmail()); });
        }

        // Fetch the existing user and update only changed fields
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) {
            existing.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null && !dto.getLastName().isEmpty()) {
            existing.setLastName(dto.getLastName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            existing.setEmail(dto.getEmail());
        }

        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }

        // Mark as updated and persist changes to the database
        existing.setUpdatedAt(LocalDateTime.now());
        userRepository.save(existing);

        return convertToResponseDTO(existing);
    }

    /**
     * Convert a User entity into a response DTO without sensitive data.
     *
     * @param user the User entity from the database
     * @return a safe UserResponseDTO for API responses
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus() != null ? user.getStatus() : User.UserStatus.ACTIVE)
                .build();
    }
}
