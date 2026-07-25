package com.taskflow.controller;

import com.taskflow.dto.*;
import com.taskflow.entity.Task;
import com.taskflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Task management operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new task with validation.
     *
     * @param dto the TaskDTO containing creation data
     * @return ResponseEntity containing the created TaskResponseDTO with 201 status
     */
    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@RequestBody TaskDTO dto) {
        try {
            TaskResponseDTO newTask = taskService.create(dto);
            return new ResponseEntity<>(newTask, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieve a specific task by its unique ID.
     *
     * @param id the task identifier to look up
     * @return ResponseEntity containing TaskResponseDTO or 404 if not found
     */
    @GetMapping("/byId/{id}")
    public ResponseEntity<TaskResponseDTO> getById(@PathVariable Long id) {
        TaskResponseDTO task = taskService.getById(id);
        return task != null ? new ResponseEntity<>(task, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Search for tasks by title or description.
     *
     * @param query the search term to match against
     * @return ResponseEntity containing list of matching TaskResponseDTOs with 200 status
     */
    @GetMapping("/search")
    public ResponseEntity<java.util.List<TaskResponseDTO>> search(@RequestParam String query) {
        java.util.List<TaskResponseDTO> tasks = taskService.search(query);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * Retrieve all active tasks with pagination.
     *
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<TaskResponseDTO> with 200 status
     */
    @GetMapping("/all")
    public ResponseEntity<Page<TaskResponseDTO>> findAll(Pageable pageable) {
        return new ResponseEntity<>(taskService.findAll(pageable), HttpStatus.OK);
    }

    /**
     * Filter and paginate tasks by project ID and status.
     *
     * @param projectId the project ID to filter within
     * @param status optional TaskStatus to filter by (null = all statuses)
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<TaskResponseDTO> with 200 status
     */
    @GetMapping("/byProject")
    public ResponseEntity<Page<TaskResponseDTO>> findByProjectAndStatus(
            @RequestParam Long projectId,
            @RequestParam(required = false) Task.TaskStatus status,
            Pageable pageable) {
        return new ResponseEntity<>(taskService.findByProjectAndStatus(projectId, 
                status != null ? status : Task.TaskStatus.TODO, pageable), HttpStatus.OK);
    }

    /**
     * Filter and paginate tasks by priority.
     *
     * @param priority the TaskPriority to filter by
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<TaskResponseDTO> with 200 status
     */
    @GetMapping("/byPriority")
    public ResponseEntity<Page<TaskResponseDTO>> findByPriority(
            @RequestParam Task.TaskPriority priority, Pageable pageable) {
        return new ResponseEntity<>(taskService.findByPriority(priority, pageable), HttpStatus.OK);
    }

    /**
     * Filter and paginate tasks by assignee.
     *
     * @param assigneeId the assignee user ID to filter by
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<TaskResponseDTO> with 200 status
     */
    @GetMapping("/byAssignee")
    public ResponseEntity<Page<TaskResponseDTO>> findByAssignee(
            @RequestParam Long assigneeId, Pageable pageable) {
        return new ResponseEntity<>(taskService.findByAssignee(assigneeId, pageable), HttpStatus.OK);
    }

    /**
     * Filter and paginate tasks by due date range.
     *
     * @param fromDate the start of the due date range
     * @param toDate the end of the due date range
     * @return ResponseEntity containing list of TaskResponseDTOs with 200 status
     */
    @GetMapping("/byDueDateRange")
    public ResponseEntity<java.util.List<TaskResponseDTO>> findByDueDateRange(
            @RequestParam java.time.LocalDateTime fromDate,
            @RequestParam java.time.LocalDateTime toDate) {
        return new ResponseEntity<>(taskService.findByDueDateRange(fromDate, toDate), HttpStatus.OK);
    }

    /**
     * Filter and paginate tasks by due date.
     *
     * @param dueDate the specific due date to filter by
     * @param pageable pagination information for results
     * @return ResponseEntity containing Page<TaskResponseDTO> with 200 status
     */
    @GetMapping("/byDueDate")
    public ResponseEntity<Page<TaskResponseDTO>> findByDueDate(
            @RequestParam java.time.LocalDate dueDate, Pageable pageable) {
        return new ResponseEntity<>(taskService.findByDueDate(dueDate, pageable), HttpStatus.OK);
    }

    /**
     * Update an existing task's information.
     *
     * @param id the task identifier to update
     * @param dto the TaskDTO containing updated fields
     * @return ResponseEntity containing the updated TaskResponseDTO with 200 status
     */
    @PutMapping("/byId/{id}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long id, @RequestBody TaskDTO dto) {
        try {
            TaskResponseDTO updatedTask = taskService.update(id, dto);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a specific task by its ID.
     *
     * @param id the task identifier to remove from PostgreSQL
     * @return ResponseEntity with 204 No Content on success, 404 if not found
     */
    @DeleteMapping("/byId/{id}")
    public ResponseEntity<java.lang.Void> delete(@PathVariable Long id) {
        try {
            taskService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            throw e;
        }
    }
}
