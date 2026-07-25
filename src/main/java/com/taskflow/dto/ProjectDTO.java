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
     * ID of the user who owns/creates this project (required).
     */
    @jakarta.validation.constraints.NotNull(message = "Owner ID is required")
    private Long ownerId;

    /**
     * Description of the project (optional).
     */
    private String description;

    /**
     * Status: ACTIVE, ARCHIVED, or DELETED.
     * Using fully qualified entity reference to avoid ambiguity with DTO's local ProjectStatus enum.
     */
    private com.taskflow.entity.Project.ProjectStatus status = com.taskflow.entity.Project.ProjectStatus.ACTIVE;

    /**
     * Priority level of the project.
     * Using fully qualified entity reference to avoid ambiguity with DTO's local ProjectPriority enum.
     */
    private com.taskflow.entity.Project.ProjectPriority priority = com.taskflow.entity.Project.ProjectPriority.MEDIUM;
}
