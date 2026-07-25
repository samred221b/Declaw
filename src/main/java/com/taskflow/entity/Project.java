package com.taskflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Project entity representing a container for Tasks and Comments.
 */
@Entity
@Table(name = "projects")
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
public class Project implements Serializable {

    /**
     * Unique identifier for the project.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Display name of the project.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Description of the project.
     */
    @Lob
    @Column(name = "description", nullable = true)
    private String description;

    /**
     * Owner (created by) of the project.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User owner;

    /**
     * Status: ACTIVE, ARCHIVED, or DELETED.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    /**
     * Priority level of the project (LOW, MEDIUM, HIGH, CRITICAL).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private ProjectPriority priority = ProjectPriority.MEDIUM;

    /**
     * Timestamp when the project was created.
     */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update to the project.
     */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    /**
     * Lazy collection of Tasks associated with this Project.
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    // Constructors for JPA
    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                '}';
    }

    /**
     * Enum representing possible project statuses.
     */
    public enum ProjectStatus {
        ACTIVE,
        ARCHIVED,
        DELETED
    }

    /**
     * Enum representing priority levels for projects.
     */
    public enum ProjectPriority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
