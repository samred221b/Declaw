package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Project creation and update operations.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    /**
     * Display name of the project.
     *
     * @NotBlank ensures a non-empty project name
     */
    @NotBlank(message = "Project name is required")
    private String name;

    /**
     * Description of the project (optional).
     */
    private String description;

    /**
     * Status: ACTIVE, ARCHIVED, or DELETED.
     */
    private ProjectStatus status = ProjectStatus.ACTIVE;

    /**
     * Priority level of the project.
     */
    private ProjectPriority priority = ProjectPriority.MEDIUM;

    // Enum for project statuses
    public enum ProjectStatus {
        ACTIVE,
        ARCHIVED,
        DELETED
    }

    // Enum for project priorities
    public enum ProjectPriority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
