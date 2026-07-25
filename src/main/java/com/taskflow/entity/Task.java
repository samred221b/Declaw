package com.taskflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Task entity representing work items within a Project.
 */
@Entity
@Table(name = "tasks")
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
public class Task implements Serializable {

    /**
     * Unique identifier for the task.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title of the task.
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Description of the task work to be done.
     */
    @Lob
    @Column(name = "description", nullable = true)
    private String description;

    /**
     * Status: TODO, IN_PROGRESS, REVIEW, or COMPLETED.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    /**
     * Priority of the task (LOW, MEDIUM, HIGH, URGENT).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    /**
     * Due date for the task completion.
     */
    @Column(name = "due_date", nullable = true)
    private LocalDateTime dueDate;

    /**
     * Assigned user who is responsible for this task (optional).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "assignee_id", nullable = true)
    private User assignee;

    /**
     * Owner of the task (created by) - same as project owner.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private User owner;

    /**
     * Parent project that contains this task.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    private Project project;

    /**
     * Tags for categorization and filtering (comma-separated or JSON).
     */
    @Column(name = "tags", nullable = true, columnDefinition = "TEXT")
    private String tags;

    /**
     * Timestamp when the task was created.
     */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update to the task.
     */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    // Constructors for JPA
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                '}';
    }

    /**
     * Enum representing task workflow statuses.
     */
    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        REVIEW,
        COMPLETED,
        BLOCKED
    }

    /**
     * Enum representing task priority levels.
     */
    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}
