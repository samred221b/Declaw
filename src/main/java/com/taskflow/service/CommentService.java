package com.taskflow.service;

import com.taskflow.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for Comment management operations.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final com.taskflow.repository.CommentRepository commentRepository;

    /**
     * Create a new comment with validation and persistence.
     *
     * @param dto the CommentDTO containing creation data
     * @return the created CommentResponseDTO with generated ID
     */
    public CommentResponseDTO create(CommentDTO dto) {
        // Validate input constraints
        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            throw new IllegalArgumentException("Comment content is required and cannot be empty");
        }

        // Map DTO fields to entity, setting defaults for optional attributes
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .parentId(dto.getParentId())
                .maxReplyDepth(dto.getMaxReplyDepth() != null ? dto.getMaxReplyDepth() : 5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Persist the new comment to PostgreSQL and return the response DTO
        commentRepository.save(comment);
        return convertToResponseDTO(comment);
    }

    /**
     * Retrieve a specific comment by its unique ID.
     *
     * @param id the comment identifier to look up
     * @return CommentResponseDTO if found, null otherwise
     */
    public CommentResponseDTO getById(Long id) {
        return commentRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElse(null);
    }

    /**
     * Retrieve all active comments with pagination.
     *
     * @param pageable the page and size information for paginated results
     * @return a Page of CommentResponseDTOs from PostgreSQL
     */
    public Page<CommentResponseDTO> findAll(Pageable pageable) {
        return commentRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Search for comments by content with partial matching.
     *
     * @param query the search term to match against
     * @return a list of CommentResponseDTOs matching the search criteria
     */
    public List<CommentResponseDTO> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return commentRepository.findByContent(query)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Filter and paginate comments by task ID.
     *
     * @param taskId the task ID to filter within
     * @param pageable pagination information for results
     * @return a Page of filtered CommentResponseDTOs from PostgreSQL
     */
    public Page<CommentResponseDTO> findByTask(Long taskId, Pageable pageable) {
        return commentRepository.findByTaskId(taskId, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Filter and paginate comments by user ID.
     *
     * @param userId the user ID to filter by
     * @param pageable pagination information for results
     * @return a Page of filtered CommentResponseDTOs from PostgreSQL
     */
    public Page<CommentResponseDTO> findByUser(Long userId, Pageable pageable) {
        return commentRepository.findByUserId(userId, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Retrieve nested (reply) comments by parent ID.
     *
     * @param parentId the parent comment ID to find replies for
     * @return a list of CommentResponseDTOs that are replies to the given parent
     */
    public List<CommentResponseDTO> getReplies(Long parentId) {
        return commentRepository.findByParentId(parentId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Update an existing comment's information.
     *
     * @param id the comment identifier to update
     * @param dto the CommentDTO containing updated fields
     * @return the updated CommentResponseDTO with new values saved to PostgreSQL
     */
    public CommentResponseDTO update(Long id, CommentDTO dto) {
        // Fetch existing entity and validate it exists before updating
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        // Update only the fields that are provided in the DTO
        if (dto.getContent() != null) {
            existing.setContent(dto.getContent());
        }

        if (dto.getParentId() != null) {
            existing.setParentId(dto.getParentId());
        }

        // Mark as updated and persist changes to PostgreSQL
        existing.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(existing);

        return convertToResponseDTO(existing);
    }

    /**
     * Delete a specific comment by its ID.
     *
     * @param id the comment identifier to remove from PostgreSQL
     */
    public void delete(Long id) {
        // Check if the comment exists before attempting deletion
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with id: " + id);
        }
        // Remove the comment from the database; cascades will handle related data
        commentRepository.deleteById(id);
    }

    /**
     * Delete all comments by a specific task ID (useful for cleanup operations).
     *
     * @param taskId the task whose comments should be deleted from PostgreSQL
     */
    public void deleteByTask(Long taskId) {
        // Remove all comments associated with this task from the database
        commentRepository.deleteByTaskId(taskId);
    }

    /**
     * Convert a Comment entity into a response DTO without sensitive data.
     *
     * @param comment the Comment entity from PostgreSQL
     * @return a safe CommentResponseDTO for API responses
     */
    private CommentResponseDTO convertToResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent() != null ? comment.getContent() : "")
                .parentId(comment.getParentId())
                .maxReplyDepth(comment.getMaxReplyDepth())
                .build();
    }
}
