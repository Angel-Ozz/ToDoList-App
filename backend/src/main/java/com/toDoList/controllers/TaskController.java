package com.toDoList.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toDoList.TaskPriority;
import com.toDoList.exceptions.EntityNotFoundException;
import com.toDoList.models.Tasks;
import com.toDoList.services.TaskRepository;

import jakarta.validation.Valid;

/**
 * REST Controller for managing tasks.
 */
@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/todos")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository taskRepository;

    /**
     * Constructor to inject the task repository.
     *
     * @param taskRepository the task repository
     */
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Retrieves all tasks with pagination and optional filters.
     *
     * @param page      the page number (default is 0)
     * @param size      the page size (default is 10)
     * @param sortBy    the field to sort by (optional)
     * @param filterBy  the field to filter by (optional)
     * @param priority  the task priority (optional)
     * @param completed the completion status of the task (optional)
     * @param taskName  the name of the task (optional)
     * @return a list of tasks
     */
    @GetMapping("")
    public ResponseEntity<List<Tasks>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String taskName) {
        
        // Retrieve tasks from the repository with the given parameters
        List<Tasks> tasks = taskRepository.findAll(page, size, sortBy, filterBy, priority, completed, taskName);
        
        // If no tasks are found, return a 204 No Content status
        if (tasks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        // Return the list of tasks with a 200 OK status
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task
     * @return the found task or a 404 status if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tasks> findById(@PathVariable Integer id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    // Log a warning if the task is not found
                    logger.warn("Task with ID {} not found", id);
                    // Throw an exception to return a 404 status
                    throw new EntityNotFoundException("Task with ID " + id + " not found", id);
                });
    }

    /**
     * Retrieves the average completion time of all tasks.
     *
     * @return the average completion time
     */
    @GetMapping("/avg-done-time")
    public ResponseEntity<Double> getAverageCompletionTime() {
        // Calculate the average completion time from the repository
        double avgTime = taskRepository.getAverageCompletionTime();
        // Return the average time with a 200 OK status
        return new ResponseEntity<>(avgTime, HttpStatus.OK);
    }

    /**
     * Retrieves the average completion time of tasks by priority.
     *
     * @return a map with the task priority and its average completion time
     */
    @GetMapping("/avg-done-time-priorities")
    public ResponseEntity<Map<TaskPriority, Double>> getAverageCompletionTimePerPriority() {
        // Calculate the average completion time per priority from the repository
        Map<TaskPriority, Double> avgTimes = taskRepository.getAverageCompletionTimePerPriority();
        // Return the map with a 200 OK status
        return new ResponseEntity<>(avgTimes, HttpStatus.OK);
    }

    /**
     * Creates a new task.
     *
     * @param task the task to create
     * @return a 201 status if the task is created successfully
     */
    @PostMapping("")
    public ResponseEntity<Void> create(@Valid @RequestBody Tasks task) {
        // Create the task in the repository
        taskRepository.create(task);
        // Log the creation of the task
        logger.info("Task created: {}", task);
        // Return a 201 Created status with the location of the new task
        return ResponseEntity.created(URI.create("/todos/" + task.getId())).build();
    }

    /**
     * Marks a task as completed.
     *
     * @param id the ID of the task
     * @return a 200 status if the task is marked as completed, or 404 if not found
     */
    @Transactional

    @PatchMapping("/{id}/done")
    public ResponseEntity<Void> markTaskAsDone(@PathVariable Integer id) {
        return taskRepository.markAsDone(id)
                .map(task -> {
                    logger.info("Task with ID {} marked as done", id);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseThrow(() -> {
                    logger.warn("Task with ID {} not found for marking as done", id);
                    throw new EntityNotFoundException("Task with ID " + id + " not found", id);
                });

    }

    /**
     * Marks a task as not completed.
     *
     * @param id the ID of the task
     * @return a 200 status if the task is marked as not completed, or 404 if not found
     */
    @Transactional //used for possible db implementation 

    @PatchMapping("/{id}/undone")
    public ResponseEntity<Void> markTaskAsUnDone(@PathVariable Integer id) {
        return taskRepository.markAsUnDone(id)
                .map(task -> {
                    logger.info("Task with ID {} marked as undone", id);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseThrow(() -> {
                    logger.warn("Task with ID {} not found for marking as undone", id);
                    throw new EntityNotFoundException("Task with ID " + id + " not found", id);
                });

} 

    /**
     * Updates a task.
     *
     * @param task the updated task
     * @param id   the ID of the task to update
     * @return a 200 status if the task is updated successfully
     */
    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody Tasks task, @PathVariable Integer id) {
        if (!taskRepository.findById(id).isPresent()) {
            // Log a warning if the task is not found
            logger.warn("Task with ID {} not found for update", id);
            // Throw an exception to return a 404 status
            throw new EntityNotFoundException("Task with ID " + id + " not found", id);
        }
        // Update the task in the repository
        taskRepository.patchUpdate(id, task);
        // Log the task update
        logger.info("Task with ID {} updated successfully", id);
        // Return a 200 OK status
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a task.
     *
     * @param id the ID of the task to delete
     * @return a 204 status if the task is deleted successfully
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!taskRepository.findById(id).isPresent()) {
            // Log a warning if the task is not found
            logger.warn("Task with ID {} not found for deletion", id);
            // Throw an exception to return a 404 status
            throw new EntityNotFoundException("Task with ID " + id + " not found", id);
        }
        // Delete the task from the repository
        taskRepository.delete(id);
        // Log the task deletion
        logger.info("Task with ID {} deleted successfully", id);
        // Return a 204 No Content status
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}