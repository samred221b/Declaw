package com.taskflow.controller;

import com.taskflow.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Register a new user with the system.
     *
     * @param dto the UserDTO containing registration data
     * @return ResponseEntity containing the created UserResponseDTO with 201 status
     */
    @PostMapping("register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserDTO dto) {
        try {
            UserResponseDTO newUser = userService.register(dto);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieve a specific user by their unique identifier.
     *
     * @param id the user ID to look up
     * @return ResponseEntity containing UserResponseDTO or 404 if not found
     */
    @GetMapping("/byId/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        UserResponseDTO user = userService.getById(id);
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieve a specific user by their username.
     *
     * @param username the unique username to look up
     * @return ResponseEntity containing UserResponseDTO or 404 if not found
     */
    @GetMapping("/byUsername/{username}")
    public ResponseEntity<UserResponseDTO> getByUsername(@PathVariable String username) {
        UserResponseDTO user = userService.getByUsername(username);
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieve a specific user by their email address.
     *
     * @param email the unique email to look up
     * @return ResponseEntity containing UserResponseDTO or 404 if not found
     */
    @GetMapping("/byEmail/{email}")
    public ResponseEntity<UserResponseDTO> getByEmail(@PathVariable String email) {
        UserResponseDTO user = userService.getByEmail(email);
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Update an existing user's profile information.
     *
     * @param id the user ID to update
     * @param dto the UserDTO containing updated fields
     * @return ResponseEntity containing the updated UserResponseDTO with 200 status
     */
    @PutMapping("/byId/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        try {
            UserResponseDTO updatedUser = userService.update(id, dto);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieve a paginated list of all users.
     *
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<UserResponseDTO> with 200 status
     */
    @GetMapping("/all")
    public ResponseEntity<Page<UserResponseDTO>> getAll(Pageable pageable) {
        // In a real system, you would only expose this to authorized admins/users
        Page<UserResponseDTO> users = userService.getAll(pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
