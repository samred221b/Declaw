package com.taskflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Task representation in API responses.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {

    /**
     * Unique identifier for the task.
     */
    private Long id;

    /**
     * Title of the task.
     */
    @Builder.Default
    private String title = "";

    /**
     * Description of the task (may be truncated in UI).
     */
    @Builder.Default
    private String description = "";

    /**
     * Status: TODO, IN_PROGRESS, REVIEW, COMPLETED, or BLOCKED.
     */
    private TaskStatus status = TaskStatus.TODO;

    /**
     * Priority of the task.
     */
    private TaskPriority priority = TaskPriority.MEDIUM;

    /**
     * Due date for completion (may be null).
     */
    @Builder.Default
    private java.time.LocalDateTime dueDate = null;

    /**
     * Assigned user ID.
     */
    private Long assigneeId;

    /**
     * Tags for categorization and filtering.
     */
    private String tags;

    // Enum for task statuses
    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        REVIEW,
        COMPLETED,
        BLOCKED
    }

    // Enum for task priorities
    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}
