package com.taskflow.repository;

import com.taskflow.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project entities.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find projects by owner user ID with pagination.
     *
     * @param ownerId the owner's user ID
     * @param pageable the page and size information
     * @return a paginated list of projects owned by the given user
     */
    Page<Project> findByOwnerId(Long ownerId, Pageable pageable);

    /**
     * Find all active projects with pagination.
     *
     * @param pageable the page and size information
     * @return a paginated list of active projects
     */
    Page<Project> findActiveProjects(Pageable pageable);

    /**
     * Search for projects by name or description (partial match).
     *
     * @param query the search term
     * @return a list of projects matching the search query
     */
    @Query("SELECT p FROM Project p " +
            "WHERE LOWER(p.name) LIKE %:query% OR LOWER(p.description) LIKE %:query%")
    List<Project> findByNameOrDescription(String query);

    /**
     * Find projects by status with pagination.
     *
     * @param status the project status to filter by
     * @param pageable the page and size information
     * @return a paginated list of projects with the given status
     */
    Page<Project> findByStatus(Project.ProjectStatus status, Pageable pageable);

    /**
     * Find projects by priority with pagination.
     *
     * @param priority the project priority to filter by
     * @param pageable the page and size information
     * @return a paginated list of projects with the given priority
     */
    Page<Project> findByPriority(Project.ProjectPriority priority, Pageable pageable);

    /**
     * Find all active projects sorted by creation date descending.
     *
     * @param limit maximum number of results to return (default 100)
     * @return a list of recently created active projects
     */
    @Query("SELECT p FROM Project p " +
            "WHERE p.status = 'ACTIVE' " +
            "ORDER BY p.createdAt DESC")
    List<Project> findRecentlyCreatedActiveProjects(Integer limit);

    /**
     * Find a project by its ID and owner.
     *
     * @param projectId the project ID to search for
     * @param ownerId the owner's user ID
     * @return an Optional containing the project if found, empty otherwise
     */
    Optional<Project> findByProjectIdAndOwnerId(Long projectId, Long ownerId);
}
