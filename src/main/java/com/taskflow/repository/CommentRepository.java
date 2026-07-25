package com.taskflow.repository;

import com.taskflow.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Comment entities.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find comments by task ID with pagination and optional parent filter.
     *
     * @param taskId the task ID to search within
     * @param pageable the page and size information
     * @return a paginated list of comments for the given task
     */
    Page<Comment> findByTaskId(Long taskId, Pageable pageable);

    /**
     * Find nested (reply) comments by parent ID.
     *
     * @param parentId the parent comment ID to search for replies
     * @return a list of reply comments under the given parent
     */
    List<Comment> findByParentId(Long parentId);

    /**
     * Search for comments by content (partial match).
     *
     * @param query the search term
     * @return a list of comments matching the search query
     */
    @Query("SELECT c FROM Comment c " +
            "WHERE LOWER(c.content) LIKE %:query%")
    List<Comment> findByContent(String query);

    /**
     * Find comments by user ID with pagination.
     *
     * @param userId the user ID to search for
     * @param pageable the page and size information
     * @return a paginated list of comments made by the given user
     */
    Page<Comment> findByUserId(Long userId, Pageable pageable);

    /**
     * Find all active (non-deleted) comments across all tasks.
     *
     * @param pageable the page and size information
     * @return a paginated list of active comments
     */
    Page<Comment> findAll(Pageable pageable);

    /**
     * Delete comments by task ID (cascade is handled by JPA but explicit delete for cleanup).
     *
     * @param taskId the task whose comments should be deleted
     * @return number of comments deleted
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.task.id = :taskId")
    int deleteByTaskId(Long taskId);
}
