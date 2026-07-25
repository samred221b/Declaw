package com.taskflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Project representation in API responses.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {

    /**
     * Unique identifier for the project.
     */
    private Long id;

    /**
     * Display name of the project.
     */
    private String name;

    /**
     * Description of the project (may be truncated in UI).
     */
    @Builder.Default
    private String description = "";

    /**
     * Owner username who created this project.
     */
    private String ownerUsername;

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
