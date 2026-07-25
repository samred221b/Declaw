package com.taskflow.service;

import com.taskflow.dto.*;
import com.taskflow.entity.Project;
import com.taskflow.entity.User;
import com.taskflow.repository.GlobalIdRepository;
import com.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for Project management operations.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final com.taskflow.repository.ProjectRepository projectRepository;
    private final GlobalIdRepository globalIdRepository;
    private final UserRepository userRepository;

    /**
     * Create a new project with validation and persistence.
     *
     * @param dto the ProjectDTO containing creation data
     * @return the created ProjectResponseDTO with generated ID
     */
    public ProjectResponseDTO create(ProjectDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Project data cannot be null");
        }
        // Validate input constraints
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name is required and cannot be empty");
        }

        if (dto.getOwnerId() == null) {
            throw new IllegalArgumentException("Owner ID is required to create a project.");
        }

        // Resolve the owner
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + dto.getOwnerId()));

        // Map DTO fields to entity, setting defaults for optional attributes
        Project project = Project.builder()
                .name(dto.getName())
                .description(dto.getDescription() != null ? dto.getDescription() : "")
                .owner(owner)
                .status(dto.getStatus() != null ? dto.getStatus() : Project.ProjectStatus.ACTIVE)
                .priority(dto.getPriority() != null ? dto.getPriority() : Project.ProjectPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Persist the new project to PostgreSQL and return the response DTO
        projectRepository.save(project);
        return convertToResponseDTO(project);
    }

    /**
     * Retrieve a specific project by its unique ID.
     *
     * @param id the project identifier to look up
     * @return ProjectResponseDTO if found, null otherwise
     */
    public ProjectResponseDTO getById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElse(null);
    }

    /**
     * Retrieve all active projects with pagination.
     *
     * @param pageable the page and size information for paginated results
     * @return a Page of ProjectResponseDTOs from PostgreSQL
     */
    public Page<ProjectResponseDTO> findAll(Pageable pageable) {
        return projectRepository.findActiveProjects(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Search for projects by name or description with partial matching.
     *
     * @param query the search term to match against
     * @return a list of ProjectResponseDTOs matching the search criteria
     */
    public List<ProjectResponseDTO> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return projectRepository.findByNameOrDescription(query)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Filter and paginate projects by status.
     *
     * @param status the ProjectStatus to filter by
     * @param pageable pagination information for results
     * @return a Page of filtered ProjectResponseDTOs from PostgreSQL
     */
    public Page<ProjectResponseDTO> findByStatus(Project.ProjectStatus status, Pageable pageable) {
        return projectRepository.findByStatus(status, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Filter and paginate projects by priority level.
     *
     * @param priority the ProjectPriority to filter by
     * @param pageable pagination information for results
     * @return a Page of filtered ProjectResponseDTOs from PostgreSQL
     */
    public Page<ProjectResponseDTO> findByPriority(Project.ProjectPriority priority, Pageable pageable) {
        return projectRepository.findByPriority(priority, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Update an existing project's information.
     *
     * @param id the project identifier to update
     * @param dto the ProjectDTO containing updated fields
     * @return the updated ProjectResponseDTO with new values saved to PostgreSQL
     */
    public ProjectResponseDTO update(Long id, ProjectDTO dto) {
        // Fetch existing entity and validate it exists before updating
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        // Update only the fields that are provided in the DTO
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            existing.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }

        if (dto.getPriority() != null) {
            existing.setPriority(dto.getPriority());
        }

        // Mark as updated and persist changes to PostgreSQL
        existing.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(existing);

        return convertToResponseDTO(existing);
    }

    /**
     * Delete a specific project by its ID.
     *
     * @param id the project identifier to remove from PostgreSQL
     */
    public void delete(Long id) {
        // Check if the project exists before attempting deletion
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Project not found with id: " + id);
        }
        // Remove the project from the database; cascades will handle related data
        projectRepository.deleteById(id);
    }

    /**
     * Convert a Project entity into a response DTO without sensitive data.
     *
     * @param project the Project entity from PostgreSQL
     * @return a safe ProjectResponseDTO for API responses
     */
    private ProjectResponseDTO convertToResponseDTO(Project project) {
        Project.ProjectStatus entityStatus = project.getStatus() != null ? project.getStatus() : Project.ProjectStatus.ACTIVE;
        Project.ProjectPriority entityPriority = project.getPriority() != null ? project.getPriority() : Project.ProjectPriority.MEDIUM;
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription() != null ? project.getDescription() : "")
                .ownerUsername(project.getOwner() != null ? project.getOwner().getUsername() : null)
                .status(ProjectResponseDTO.ProjectStatus.valueOf(entityStatus.name()))
                .priority(ProjectResponseDTO.ProjectPriority.valueOf(entityPriority.name()))
                .build();
    }
}
