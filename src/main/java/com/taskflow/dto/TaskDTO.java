package com.taskflow.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Task creation and update operations.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    /**
     * Title of the task.
     *
     * @NotBlank ensures a non-empty title
     * @Size limits the title to 200 characters
     */
    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Task title must not exceed 200 characters")
    private String title;

    /**
     * Description of the task work to be done (optional).
     */
    private String description;

    /**
     * Status: TODO, IN_PROGRESS, REVIEW, COMPLETED, or BLOCKED.
     */
    @NotNull(message = "Task status is required")
    private TaskStatus status = TaskStatus.TODO;

    /**
     * Priority of the task.
     */
    @NotNull(message = "Task priority is required")
    private TaskPriority priority = TaskPriority.MEDIUM;

    /**
     * Due date for task completion (optional).
     */
    private java.time.LocalDateTime dueDate;

    /**
     * Assigned user ID (optional - will be populated by assignment endpoint).
     */
    @NotNull(message = "Assignee is required")
    private Long assigneeId;

    /**
     * Tags for categorization and filtering (comma-separated string).
     */
    @NotBlank(message = "At least one tag is required")
    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    /**
     * Enum for task workflow statuses.
     */
    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        REVIEW,
        COMPLETED,
        BLOCKED
    }

    /**
     * Enum for task priority levels.
     */
    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}
