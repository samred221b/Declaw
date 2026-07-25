package com.taskflow.repository;

import com.taskflow.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entities.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find tasks by project ID with pagination and optional status filter.
     *
     * @param projectId the project ID to search within
     * @param status optional status to filter by (null = all statuses)
     * @param pageable the page and size information
     * @return a paginated list of tasks in the given project with optional status filter
     */
    Page<Task> findByProjectIdAndStatus(Long projectId, Task.TaskStatus status, Pageable pageable);

    /**
     * Find all active tasks with pagination across all projects.
     *
     * @param pageable the page and size information
     * @return a paginated list of active tasks (not COMPLETED or DELETED)
     */
    @Query("SELECT t FROM Task t WHERE t.status != 'COMPLETED'")
    Page<Task> findActiveTasks(Pageable pageable);

    /**
     * Find tasks by assignee user ID with pagination.
     *
     * @param assigneeId the assignee's user ID
     * @param pageable the page and size information
     * @return a pagulated list of tasks assigned to the given user
     */
    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    /**
     * Search for tasks by title or description (partial match).
     *
     * @param query the search term
     * @return a list of tasks matching the search query
     */
    @Query("SELECT t FROM Task t " +
            "WHERE LOWER(t.title) LIKE %:query% OR t.description LIKE %:query%")
    List<Task> findByNameOrDescription(String query);

    /**
     * Find tasks by priority with pagination.
     *
     * @param priority the task priority to filter by
     * @param pageable the page and size information
     * @return a paginated list of tasks with the given priority
     */
    Page<Task> findByPriority(Task.TaskPriority priority, Pageable pageable);

    /**
     * Find overdue tasks (due date in the past) that are not COMPLETED.
     *
     * @param pageable the page and size information
     * @return a paginated list of overdue tasks
     */
    @Query("SELECT t FROM Task t " +
            "WHERE COALESCE(t.dueDate, '1970-01-01T00:00:00') < CURRENT_DATE " +
            "AND t.status != 'COMPLETED'")
    Page<Task> findOverdueTasks(Pageable pageable);

    /**
     * Find tasks by tag (supports partial matching on comma-separated tags).
     *
     * @param tagName the tag to search for
     * @return a list of tasks with the given tag
     */
    @Query("SELECT t FROM Task t " +
            "WHERE LOWER(t.tags) LIKE %:tagName%")
    List<Task> findByTag(String tagName);

    /**
     * Find tasks by due date range for filtering purposes.
     *
     * @param fromDate the start of the date range
     * @param toDate the end of the date range
     * @return a list of tasks with due dates in the specified range
     */
    @Query("SELECT t FROM Task t " +
            "WHERE COALESCE(t.dueDate, '1970-01-01T00:00:00') BETWEEN :fromDate AND :toDate")
    List<Task> findByDueDateRange(java.time.LocalDateTime fromDate, java.time.LocalDateTime toDate);

    /**
     * Find tasks by due date with pagination.
     *
     * @param dueDate the specific due date to search for
     * @param pageable the page and size information
     * @return a paginated list of tasks due on the given date
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate >= :start AND t.dueDate < :end ORDER BY t.priority")
    Page<Task> findByDueDate(java.time.LocalDateTime start, java.time.LocalDateTime end, Pageable pageable);

    /**
     * Find all tasks sorted by priority (URGENT first) then due date.
     *
     * @param limit maximum number of results to return (default 100)
     * @return a list of tasks ordered by priority and due date
     */
    @Query("SELECT t FROM Task t " +
            "ORDER BY CASE t.priority WHEN 'URGENT' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 3 ELSE 4 END, " +
            "COALESCE(t.dueDate, '9999-12-31T00:00:00')")
    List<Task> findTasksByPriorityAndDueDate(Integer limit);

    /**
     * Bulk update task status.
     *
     * @param ids list of task IDs to update
     * @param newStatus the new status to set for all specified tasks
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.id IN :ids")
    int bulkUpdateStatus(List<Long> ids, Task.TaskStatus newStatus);

    /**
     * Find tasks by project ID and priority.
     *
     * @param projectId the project ID to search within
     * @param priority the task priority to filter by
     * @return a list of tasks in the given project with the specified priority
     */
    List<Task> findByProjectIdAndPriority(Long projectId, Task.TaskPriority priority);
}
