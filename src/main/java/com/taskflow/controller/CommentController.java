package com.taskflow.controller;

import com.taskflow.dto.*;
import com.taskflow.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Comment management operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * Create a new comment with validation.
     *
     * @param dto the CommentDTO containing creation data
     * @return ResponseEntity containing the created CommentResponseDTO with 201 status
     */
    @PostMapping("/tasks/{taskId}")
    public ResponseEntity<CommentResponseDTO> create(
            @PathVariable Long taskId,
            @RequestParam Long userId,
            @RequestBody CommentDTO dto) {
        try {
            CommentResponseDTO newComment = commentService.create(dto, taskId, userId);
            return new ResponseEntity<>(newComment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieve a specific comment by its unique ID.
     *
     * @param id the comment identifier to look up
     * @return ResponseEntity containing CommentResponseDTO or 404 if not found
     */
    @GetMapping("/byId/{id}")
    public ResponseEntity<CommentResponseDTO> getById(@PathVariable Long id) {
        CommentResponseDTO comment = commentService.getById(id);
        return comment != null ? new ResponseEntity<>(comment, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Search for comments by content.
     *
     * @param query the search term to match against
     * @return ResponseEntity containing list of matching CommentResponseDTOs with 200 status
     */
    @GetMapping("/search")
    public ResponseEntity<java.util.List<CommentResponseDTO>> search(@RequestParam String query) {
        java.util.List<CommentResponseDTO> comments = commentService.search(query);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * Retrieve all active comments with pagination.
     *
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<CommentResponseDTO> with 200 status
     */
    @GetMapping("/all")
    public ResponseEntity<Page<CommentResponseDTO>> findAll(Pageable pageable) {
        return new ResponseEntity<>(commentService.findAll(pageable), HttpStatus.OK);
    }

    /**
     * Filter and paginate comments by task ID.
     *
     * @param taskId the task ID to filter within
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<CommentResponseDTO> with 200 status
     */
    @GetMapping("/byTask")
    public ResponseEntity<Page<CommentResponseDTO>> findByTask(
            @RequestParam Long taskId, Pageable pageable) {
        return new ResponseEntity<>(commentService.findByTask(taskId, pageable), HttpStatus.OK);
    }

    /**
     * Filter and paginate comments by user ID.
     *
     * @param userId the user ID to filter by
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<CommentResponseDTO> with 200 status
     */
    @GetMapping("/byUser")
    public ResponseEntity<Page<CommentResponseDTO>> findByUser(
            @RequestParam Long userId, Pageable pageable) {
        return new ResponseEntity<>(commentService.findByUser(userId, pageable), HttpStatus.OK);
    }

    /**
     * Retrieve nested (reply) comments by parent ID.
     *
     * @param parentId the parent comment ID to find replies for
     * @return ResponseEntity containing list of CommentResponseDTOs with 200 status
     */
    @GetMapping("/replies")
    public ResponseEntity<java.util.List<CommentResponseDTO>> getReplies(@RequestParam Long parentId) {
        java.util.List<CommentResponseDTO> replies = commentService.getReplies(parentId);
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

    /**
     * Update an existing comment's information.
     *
     * @param id the comment identifier to update
     * @param dto the CommentDTO containing updated fields
     * @return ResponseEntity containing the updated CommentResponseDTO with 200 status
     */
    @PutMapping("/byId/{id}")
    public ResponseEntity<CommentResponseDTO> update(@PathVariable Long id, @RequestBody CommentDTO dto) {
        try {
            CommentResponseDTO updatedComment = commentService.update(id, dto);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a specific comment by its ID.
     *
     * @param id the comment identifier to remove from PostgreSQL
     * @return ResponseEntity with 204 No Content on success, 404 if not found
     */
    @DeleteMapping("/byId/{id}")
    public ResponseEntity<java.lang.Void> delete(@PathVariable Long id) {
        try {
            commentService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            throw e;
        }
    }
}
