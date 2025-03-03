package com.toDoList.services;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.toDoList.TaskPriority;
import com.toDoList.exceptions.EntityNotFoundException;
import com.toDoList.models.Tasks;

import jakarta.annotation.PostConstruct;

@Repository
public class TaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskRepository.class);
    private List<Tasks> tasks = new ArrayList<>();
    private int currentId = 0;

    /**
     * Finds all tasks with optional filtering and sorting.
     *
     * @param page      the page number for pagination
     * @param size      the page size for pagination
     * @param sortBy    the field to sort by (optional)
     * @param filterBy  the field to filter by (optional)
     * @param priority  the task priority to filter by (optional)
     * @param completed the completion status to filter by (optional)
     * @param taskName  the task name to filter by (optional)
     * @return a list of tasks matching the criteria
     */
    public List<Tasks> findAll(int page, int size, String sortBy, String filterBy, String priority, Boolean completed, String taskName) {

        // Create a stream from the list of tasks
        Stream<Tasks> taskStream = tasks.stream();

        // Filter tasks by completion status if provided
        if (completed != null) {
            taskStream = taskStream.filter(task -> task.getCompleted().equals(completed));
        }

        // Filter tasks by task name if provided (case insensitive partial match)
        if (taskName != null && !taskName.isEmpty()) {
            taskStream = taskStream.filter(task -> task.getTaskName().toLowerCase().contains(taskName.toLowerCase()));
        }

        // Filter tasks by priority if provided
        if (priority != null) {
            try {
                TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
                taskStream = taskStream.filter(task -> task.getTaskPriority() == taskPriority);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid priority filter: {}", priority);
                throw new IllegalArgumentException("Invalid priority value: " + priority);
            }
        }

        // Sort tasks by the specified field if provided
        if ("priority".equalsIgnoreCase(sortBy)) {
            taskStream = taskStream.sorted(Comparator.comparing(Tasks::getTaskPriority));
        } else if ("taskDueDate".equalsIgnoreCase(sortBy)) {
            taskStream = taskStream.sorted(Comparator.comparing(Tasks::getTaskDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
        }

        // Apply pagination to the stream
        return taskStream
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new task.
     *
     * @param task the task to create
     */
    public void create(Tasks task) {
        // Check if the task is null or already exists
        if (task == null || findById(task.getId()).isPresent()) {
            logger.warn("Attempted to create an invalid or existing task: {}", task);
            throw new IllegalArgumentException("Task already exists or is invalid.");
        }
        // Increment the current ID and set it to the task
        currentId++;
        task.setId(currentId);
        // Add the task to the list
        tasks.add(task);
        logger.info("Task created: {}", task);
    }

    /**
     * Finds a task by its ID.
     *
     * @param id the ID of the task
     * @return an Optional containing the found task or empty if not found
     */
    public Optional<Tasks> findById(Integer id) {
        // Find the task by ID in the list
        return tasks.stream().filter(task -> task.getId().equals(id)).findFirst();
    }

    /**
     * Updates a task using partial data.
     *
     * @param id            the ID of the task to update
     * @param partialUpdate the partial data to update the task with
     * @return the updated task
     */
    public Tasks patchUpdate(Integer id, Tasks partialUpdate) {
        // Find the task by ID and update its fields if present
        return findById(id).map(existingTask -> {
            if (partialUpdate.getTaskName() != null) {
                existingTask.setTaskName(partialUpdate.getTaskName());
            }
            if (partialUpdate.getTaskPriority() != null) {
                existingTask.setTaskPriority(partialUpdate.getTaskPriority());
            }
            if (partialUpdate.getCompleted() != null) {
                existingTask.setCompleted(partialUpdate.getCompleted());
            }
            if (partialUpdate.getTaskDueDate() != null) {
                existingTask.setTaskDueDate(partialUpdate.getTaskDueDate());
            }
            logger.info("Task with ID {} updated successfully", id);
            return existingTask;
        }).orElseThrow(() -> {
            logger.warn("Task with ID {} not found for update", id);
            return new EntityNotFoundException("Task with ID " + id + " not found for update", id);
        });
    }

    /**
     * Marks a task as completed.
     *
     * @param id the ID of the task to mark as completed
     * @return an Optional containing the updated task or empty if not found
     */
    public Optional<Tasks> markAsDone(Integer id) {
        // Find the task by ID and mark it as completed if present
        return findById(id).map(task -> {
            if (!task.getCompleted()) {
                task.setCompleted(true);
                logger.info("Task with ID {} marked as done", id);
            }
            return task;
        }).or(() -> {
            logger.warn("Task with ID {} not found for marking as done", id);
            throw new EntityNotFoundException("Task with ID " + id + " not found for marking as done", id);
        });
    }

    /**
     * Marks a task as not completed.
     *
     * @param id the ID of the task to mark as not completed
     * @return an Optional containing the updated task or empty if not found
     */
    public Optional<Tasks> markAsUnDone(Integer id) {
        // Find the task by ID and mark it as not completed if present
        return findById(id).map(task -> {
            if (task.getCompleted()) {
                task.setCompleted(false);
                logger.info("Task with ID {} marked as undone", id);
            }
            return task;
        }).or(() -> {
            logger.warn("Task with ID {} not found for marking as undone", id);
            throw new EntityNotFoundException("Task with ID " + id + " not found for marking as undone", id);
        });
    }

    /**
     * Calculates the average completion time of all completed tasks.
     *
     * @return the average completion time in minutes
     */
    public double getAverageCompletionTime() {
        // Calculate the average completion time of completed tasks
        return tasks.stream()
                .filter(Tasks::getCompleted)
                .mapToDouble(task -> Math.floor(Duration.between(task.getCreationDate(), task.getDoneDate()).toMinutes()))
                .average()
                .orElse(0.0);
    }

    /**
     * Calculates the average completion time of tasks by priority.
     *
     * @return a map with the task priority and its average completion time
     */
    public Map<TaskPriority, Double> getAverageCompletionTimePerPriority() {
        // Calculate the average completion time per priority for completed tasks
        return tasks.stream()
                .filter(Tasks::getCompleted)
                .collect(Collectors.groupingBy(
                        Tasks::getTaskPriority,
                        Collectors.averagingDouble(task -> Math.floor(Duration.between(task.getCreationDate(), task.getDoneDate()).toMinutes()))
                ));
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     * @return true if the task was deleted successfully, false otherwise
     */
    public boolean delete(Integer id) {
        // Remove the task by ID from the list
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        if (!removed) {
            logger.warn("Task with ID {} not found for deletion", id);
            throw new EntityNotFoundException("Task with ID " + id + " not found for deletion", id);
        }
        logger.info("Task with ID {} deleted successfully", id);
        return true;
    }

    /**
     * Initializes the repository with sample data.
     */
    @PostConstruct
    public void init() {
        // Add a sample task to the list
        tasks.add(new Tasks(
                ++currentId,
                "Do a to-do list",
                TaskPriority.HIGH,
                false,
                LocalDate.now()
        ));
        logger.info("Initialized Task Repository with sample data.");
    }
}