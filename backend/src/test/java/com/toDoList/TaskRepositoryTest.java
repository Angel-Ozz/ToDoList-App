package com.toDoList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.toDoList.exceptions.EntityNotFoundException;
import com.toDoList.models.Tasks;
import com.toDoList.services.TaskRepository;

class TaskRepositoryTest {

    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new TaskRepository();
        taskRepository.init(); // Initialize with sample data
    }

    @Test
    void givenNewTask_whenCreateTask_thenTaskIsCreatedSuccessfully() {
        // Given
        Tasks task = new Tasks(null, "Test Task", TaskPriority.MEDIUM, false, LocalDate.now());

        // When
        taskRepository.create(task);
        Optional<Tasks> createdTask = taskRepository.findById(task.getId());

        // Then
        assertTrue(createdTask.isPresent());
        assertEquals(task.getId(), createdTask.get().getId());
        assertEquals("Test Task", createdTask.get().getTaskName());
        assertEquals(TaskPriority.MEDIUM, createdTask.get().getTaskPriority());
        assertFalse(createdTask.get().getCompleted());
    }

    @Test
    void givenExistingTaskId_whenFindById_thenTaskIsReturned() {
        // Given
        Tasks task = new Tasks(null, "Test Task", TaskPriority.MEDIUM, false, LocalDate.now());
        taskRepository.create(task);

        // When
        Optional<Tasks> foundTask = taskRepository.findById(task.getId());

        // Then
        assertTrue(foundTask.isPresent());
        assertEquals(task.getId(), foundTask.get().getId());
    }

    @Test
    void givenNonExistingTaskId_whenFindById_thenTaskIsNotFound() {
        // When
        Optional<Tasks> foundTask = taskRepository.findById(999);

        // Then
        assertFalse(foundTask.isPresent());
    }

    @Test
    void givenExistingTask_whenPatchUpdate_thenTaskIsUpdatedSuccessfully() {
        // Given
        Tasks task = new Tasks(null, "Original Task", TaskPriority.LOW, false, LocalDate.now());
        taskRepository.create(task);

        Tasks updateData = new Tasks(null, "Updated Task", TaskPriority.HIGH, true, LocalDate.now());

        // When
        Tasks updatedTask = taskRepository.patchUpdate(task.getId(), updateData);

        // Then
        assertEquals("Updated Task", updatedTask.getTaskName());
        assertEquals(TaskPriority.HIGH, updatedTask.getTaskPriority());
        assertTrue(updatedTask.getCompleted());
    }

    @Test
    void givenExistingTask_whenMarkAsDone_thenTaskIsMarkedAsDone() {
        // Given
        Tasks task = new Tasks(null, "Test Task", TaskPriority.MEDIUM, false, LocalDate.now());
        taskRepository.create(task);

        // When
        Optional<Tasks> updatedTask = taskRepository.markAsDone(task.getId());

        // Then
        assertTrue(updatedTask.isPresent());
        assertTrue(updatedTask.get().getCompleted());
    }

    @Test
    void givenExistingTask_whenMarkAsUnDone_thenTaskIsMarkedAsUnDone() {
        // Given
        Tasks task = new Tasks(null, "Test Task", TaskPriority.MEDIUM, true, LocalDate.now());
        taskRepository.create(task);

        // When
        Optional<Tasks> updatedTask = taskRepository.markAsUnDone(task.getId());

        // Then
        assertTrue(updatedTask.isPresent());
        assertFalse(updatedTask.get().getCompleted());
    }

    @Test
    void givenExistingTask_whenDelete_thenTaskIsDeletedSuccessfully() {
        // Given
        Tasks task = new Tasks(null, "Test Task", TaskPriority.MEDIUM, false, LocalDate.now());
        taskRepository.create(task);

        // When
        boolean isDeleted = taskRepository.delete(task.getId());

        // Then
        assertTrue(isDeleted);
        assertFalse(taskRepository.findById(task.getId()).isPresent());
    }

    @Test
    void givenNonExistingTaskId_whenDelete_thenExceptionIsThrown() {
        // Given
        int nonExistingId = 999;

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> taskRepository.delete(nonExistingId));
    }

    @Test
    void givenTasks_whenFindAllWithParams_thenTasksAreReturned() {
        // Given
        Tasks task1 = new Tasks(null, "Task 1", TaskPriority.HIGH, false, LocalDate.now());
        Tasks task2 = new Tasks(null, "Task 2", TaskPriority.MEDIUM, true, LocalDate.now());
        taskRepository.create(task1);
        taskRepository.create(task2);

        // When
        List<Tasks> tasks = taskRepository.findAll(0, 10, "priority", null, "HIGH", null, null);

        // Then
        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(1).getTaskName());
    }

    @Test
    void givenCompletedTasks_whenGetAverageCompletionTime_thenAverageTimeIsReturned() {
        // Given
        Tasks task1 = new Tasks(null, "Task 1", TaskPriority.HIGH, true, LocalDate.now());
        task1.setDoneDate(LocalDateTime.now().plusDays(1)); 

        Tasks task2 = new Tasks(null, "Task 2", TaskPriority.MEDIUM, true, LocalDate.now());
        task2.setDoneDate(LocalDateTime.now().plusDays(2)); 

        taskRepository.create(task1);
        taskRepository.create(task2);

        // When
        double avgTime = taskRepository.getAverageCompletionTime();

        // Then
        assertTrue(avgTime >= 1440.0 && avgTime <= 2880.0); 
    }

}