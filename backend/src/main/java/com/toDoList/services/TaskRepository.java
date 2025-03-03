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

    // Find all tasks with filtering and sorting
    public List<Tasks> findAll(int page, int size, String sortBy, String filterBy, String priority, Boolean completed, String taskName) {

        Stream<Tasks> taskStream = tasks.stream();

        // Filtering by completion status
        if (completed != null) {
            taskStream = taskStream.filter(task -> task.getCompleted().equals(completed));
        }

        // Filtering by task name (partial match)
        if (taskName != null && !taskName.isEmpty()) {
            taskStream = taskStream.filter(task -> task.getTaskName().toLowerCase().contains(taskName.toLowerCase()));
        }

        // Filtering by priority
        if (priority != null) {
            try {
                TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
                taskStream = taskStream.filter(task -> task.getTaskPriority() == taskPriority);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid priority filter: {}", priority);
                throw new IllegalArgumentException("Invalid priority value: " + priority);
            }
        }

        // Sorting
        if ("priority".equalsIgnoreCase(sortBy)) {
            taskStream = taskStream.sorted(Comparator.comparing(Tasks::getTaskPriority));
        } else if ("taskDueDate".equalsIgnoreCase(sortBy)) {
            taskStream = taskStream.sorted(Comparator.comparing(Tasks::getTaskDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
        }

        // Pagination
        return taskStream
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    // Create a new task
    public void create(Tasks task) {
        if (task == null || findById(task.getId()).isPresent()) {
            logger.warn("Attempted to create an invalid or existing task: {}", task);
            throw new IllegalArgumentException("Task already exists or is invalid.");
        }
        currentId++;
        task.setId(currentId);
        tasks.add(task);
        logger.info("Task created: {}", task);
    }

    // Find a task by ID
    public Optional<Tasks> findById(Integer id) {
        return tasks.stream().filter(task -> task.getId().equals(id)).findFirst();
    }

    // Update a task using patch
    public Tasks patchUpdate(Integer id, Tasks partialUpdate) {
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

    // Mark a task as completed
    public Optional<Tasks> markAsDone(Integer id) {
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

    // Mark a task as uncompleted
    public Optional<Tasks> markAsUnDone(Integer id) {
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

    // Average completion time
    public double getAverageCompletionTime() {
        return tasks.stream()
                .filter(Tasks::getCompleted)
                .mapToDouble(task -> Math.floor(Duration.between(task.getCreationDate(), task.getDoneDate()).toMinutes()))
                .average()
                .orElse(0.0);
    }

    // Average completion time per priority
    public Map<TaskPriority, Double> getAverageCompletionTimePerPriority() {
        return tasks.stream()
                .filter(Tasks::getCompleted)
                .collect(Collectors.groupingBy(
                        Tasks::getTaskPriority,
                        Collectors.averagingDouble(task -> Math.floor(Duration.between(task.getCreationDate(), task.getDoneDate()).toMinutes()))
                ));
    }

    // Delete a task
    public boolean delete(Integer id) {
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        if (!removed) {
            logger.warn("Task with ID {} not found for deletion", id);
            throw new EntityNotFoundException("Task with ID " + id + " not found for deletion", id);
        }
        logger.info("Task with ID {} deleted successfully", id);
        return true;
    }

    // Initialize repository with sample data
    @PostConstruct
    public void init() {
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
