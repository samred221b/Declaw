package com.taskflow.controller;

import com.taskflow.dto.*;
import com.taskflow.entity.Project;
import com.taskflow.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Project management operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Create a new project with validation.
     *
     * @param dto the ProjectDTO containing creation data
     * @return ResponseEntity containing the created ProjectResponseDTO with 201 status
     */
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> create(@RequestBody ProjectDTO dto) {
        try {
            ProjectResponseDTO newProject = projectService.create(dto);
            return new ResponseEntity<>(newProject, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieve a specific project by its unique ID.
     *
     * @param id the project identifier to look up
     * @return ResponseEntity containing ProjectResponseDTO or 404 if not found
     */
    @GetMapping("/byId/{id}")
    public ResponseEntity<ProjectResponseDTO> getById(@PathVariable Long id) {
        ProjectResponseDTO project = projectService.getById(id);
        return project != null ? new ResponseEntity<>(project, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Search for projects by name or description.
     *
     * @param query the search term to match against
     * @return ResponseEntity containing list of matching ProjectResponseDTOs with 200 status
     */
    @GetMapping("/search")
    public ResponseEntity<java.util.List<ProjectResponseDTO>> search(@RequestParam String query) {
        java.util.List<ProjectResponseDTO> projects = projectService.search(query);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    /**
     * Filter and paginate projects by status.
     *
     * @param status the ProjectStatus to filter by
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<ProjectResponseDTO> with 200 status
     */
    @GetMapping("/byStatus")
    public ResponseEntity<Page<ProjectResponseDTO>> findByStatus(
            @RequestParam Project.ProjectStatus status, Pageable pageable) {
        return new ResponseEntity<>(projectService.findByStatus(status, pageable), HttpStatus.OK);
    }

    /**
     * Filter and paginate projects by priority.
     *
     * @param priority the ProjectPriority to filter by
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<ProjectResponseDTO> with 200 status
     */
    @GetMapping("/byPriority")
    public ResponseEntity<Page<ProjectResponseDTO>> findByPriority(
            @RequestParam Project.ProjectPriority priority, Pageable pageable) {
        return new ResponseEntity<>(projectService.findByPriority(priority, pageable), HttpStatus.OK);
    }

    /**
     * Update an existing project's information.
     *
     * @param id the project identifier to update
     * @param dto the ProjectDTO containing updated fields
     * @return ResponseEntity containing the updated ProjectResponseDTO with 200 status
     */
    @PutMapping("/byId/{id}")
    public ResponseEntity<ProjectResponseDTO> update(@PathVariable Long id, @RequestBody ProjectDTO dto) {
        try {
            ProjectResponseDTO updatedProject = projectService.update(id, dto);
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a specific project by its ID.
     *
     * @param id the project identifier to remove from PostgreSQL
     * @return ResponseEntity with 204 No Content on success, 404 if not found
     */
    @DeleteMapping("/byId/{id}")
    public ResponseEntity<java.lang.Void> delete(@PathVariable Long id) {
        try {
            projectService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            throw e;
        }
    }
}
