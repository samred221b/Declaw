package com.taskflow.service;

import com.taskflow.dto.*;
import com.taskflow.entity.Project;
import com.taskflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for Task management operations.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Create a new task with validation and persistence.
     *
     * @param dto the TaskDTO containing creation data
     * @return the created TaskResponseDTO with generated ID
     */
    public TaskResponseDTO create(TaskDTO dto) {
        // Validate input constraints
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Task title is required and cannot be empty");
        }
        if (dto.getDescription() != null && dto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description must not contain only whitespace");
        }

        // Map DTO fields to entity, setting defaults for optional attributes
        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription() != null ? dto.getDescription() : "")
                .status(dto.getStatus() != null ? dto.getStatus() : Task.TaskStatus.TODO)
                .priority(dto.getPriority() != null ? dto.getPriority() : Task.TaskPriority.MEDIUM)
                .dueDate(dto.getDueDate())
                // Assignee will be set via a separate assignment endpoint; use null here
                .assignee(null)
                .tags(dto.getTags() != null ? dto.getTags() : "")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Persist the new task to PostgreSQL and return the response DTO
        taskRepository.save(task);
        return convertToResponseDTO(task);
    }

    /**
     * Retrieve a specific task by its unique ID.
     *
     * @param id the task identifier to look up
     * @return TaskResponseDTO if found, null otherwise
     */
    public TaskResponseDTO getById(Long id) {
        return taskRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElse(null);
    }

    /**
     * Retrieve all active tasks with pagination.
     *
     * @param pageable the page and size information for paginated results
     * @return a Page of TaskResponseDTOs from PostgreSQL
     */
    public Page<TaskResponseDTO> findAll(Pageable pageable) {
        return taskRepository.findActiveTasks(pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Search for tasks by title or description with partial matching.
     *
     * @param query the search term to match against
     * @return a list of TaskResponseDTOs matching the search criteria
     */
    public List<TaskResponseDTO> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return taskRepository.findByNameOrDescription(query)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Filter and paginate tasks by status.
     *
     * @param projectId the project ID to filter within
     * @param status the TaskStatus to filter by (null = all statuses)
     * @param pageable pagination information for results
     * @return a Page of filtered TaskResponseDTOs from PostgreSQL
     */
    public Page<TaskResponseDTO> findByProjectAndStatus(Long projectId, Task.TaskStatus status, Pageable pageable) {
        return taskRepository.findByProjectIdAndStatus(projectId, status != null ? status : Task.TaskStatus.TODO, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Filter and paginate tasks by priority level.
     *
     * @param priority the TaskPriority to filter by
     * @param pageable pagination information for results
     * @return a Page of filtered TaskResponseDTOs from PostgreSQL
     */
    public Page<TaskResponseDTO> findByPriority(Task.TaskPriority priority, Pageable pageable) {
        return taskRepository.findByPriority(priority, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Filter and paginate tasks by assignee.
     *
     * @param assigneeId the assignee user ID to filter by
     * @param pageable pagination information for results
     * @return a Page of filtered TaskResponseDTOs from PostgreSQL
     */
    public Page<TaskResponseDTO> findByAssignee(Long assigneeId, Pageable pageable) {
        return taskRepository.findByAssigneeId(assigneeId, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Filter and paginate tasks by due date range.
     *
     * @param fromDate the start of the due date range
     * @param toDate the end of the due date range
     * @return a list of TaskResponseDTOs with due dates in the specified range
     */
    public List<TaskResponseDTO> findByDueDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            return List.of();
        }
        return taskRepository.findByDueDateRange(fromDate, toDate)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Filter and paginate tasks by due date.
     *
     * @param dueDate the specific due date to filter by
     * @param pageable pagination information for results
     * @return a Page of TaskResponseDTOs due on the specified date from PostgreSQL
     */
    public Page<TaskResponseDTO> findByDueDate(java.time.LocalDate dueDate, Pageable pageable) {
        return taskRepository.findByDueDate(dueDate, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Update an existing task's information.
     *
     * @param id the task identifier to update
     * @param dto the TaskDTO containing updated fields
     * @return the updated TaskResponseDTO with new values saved to PostgreSQL
     */
    public TaskResponseDTO update(Long id, TaskDTO dto) {
        // Fetch existing entity and validate it exists before updating
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        // Update only the fields that are provided in the DTO
        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            existing.setTitle(dto.getTitle());
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

        if (dto.getDueDate() != null && !dto.getDueDate().isBefore(existing.getCreatedAt())) {
            // Prevent modifying due date to the past
            existing.setDueDate(dto.getDueDate());
        }

        if (dto.getTags() != null) {
            existing.setTags(dto.getTags());
        }

        // Mark as updated and persist changes to PostgreSQL
        existing.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(existing);

        return convertToResponseDTO(existing);
    }

    /**
     * Bulk update task statuses for efficiency when updating multiple tasks at once.
     *
     * @param ids list of task IDs to update
     * @param newStatus the new status to set for all specified tasks
     */
    public void bulkUpdateStatus(List<Long> ids, Task.TaskStatus newStatus) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        // Use batch operation to update multiple tasks efficiently in PostgreSQL
        taskRepository.bulkUpdateStatus(ids, newStatus);
    }

    /**
     * Delete a specific task by its ID.
     *
     * @param id the task identifier to remove from PostgreSQL
     */
    public void delete(Long id) {
        // Check if the task exists before attempting deletion
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        // Remove the task from the database; cascades will handle related data
        taskRepository.deleteById(id);
    }

    /**
     * Convert a Task entity into a response DTO without sensitive data.
     *
     * @param task the Task entity from PostgreSQL
     * @return a safe TaskResponseDTO for API responses
     */
    private TaskResponseDTO convertToResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle() != null ? task.getTitle() : "")
                .description(task.getDescription() != null ? task.getDescription() : "")
                .status(task.getStatus() != null ? task.getStatus() : Task.TaskStatus.TODO)
                .priority(task.getPriority() != null ? task.getPriority() : Task.TaskPriority.MEDIUM)
                .dueDate(task.getDueDate())
                .assigneeId(task.getAssigneeId())
                .tags(task.getTags() != null ? task.getTags() : "")
                .build();
    }
}
