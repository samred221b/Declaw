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
     * Using fully qualified entity reference to avoid ambiguity with DTO's local TaskStatus enum.
     */
    @NotNull(message = "Task status is required")
    private com.taskflow.entity.Task.TaskStatus status = com.taskflow.entity.Task.TaskStatus.TODO;

    /**
     * Priority of the task.
     */
    @NotNull(message = "Task priority is required")
    private com.taskflow.entity.Task.TaskPriority priority = com.taskflow.entity.Task.TaskPriority.MEDIUM;

    /**
     * Due date for task completion (optional).
     */
    private java.time.LocalDateTime dueDate;

    /**
     * Project ID this task belongs to (required on creation).
     */
    @NotNull(message = "Project ID is required")
    private Long projectId;

    /**
     * Assigned user ID (optional).
     */
    private Long assigneeId;

    /**
     * Tags for categorization and filtering (comma-separated string).
     */
    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;
}
