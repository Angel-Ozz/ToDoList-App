package com.toDoList;

import static org.junit.jupiter.api.Assertions.*;

import com.toDoList.models.Tasks;
import com.toDoList.services.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

class TaskRepositoryTest {

    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new TaskRepository();
    }

    @Test
    void testCreateTaskSuccessfully() {
        Tasks task = new Tasks(null, "Test Task", TaskPriority.MEDIUM, false, LocalDate.now());

        taskRepository.create(task);
        Optional<Tasks> createdTask = taskRepository.findById(1);

        assertTrue(createdTask.isPresent());
        assertEquals(1, createdTask.get().getId());
        assertEquals("Test Task", createdTask.get().getTaskName());
        assertEquals(TaskPriority.MEDIUM, createdTask.get().getTaskPriority());
        assertFalse(createdTask.get().getCompleted());
    }

    @Test // its useseless because the way the code handles, it will never be null
    void testCreateNullTaskThrowsException() {
        Tasks task1 = new Tasks(null, "Test Task Null", TaskPriority.MEDIUM, false, LocalDate.now());
        Tasks task2 = new Tasks(1, "Test Task", TaskPriority.LOW, false, LocalDate.now());
        taskRepository.create(task1);

        assertThrows(IllegalArgumentException.class, () -> taskRepository.create(task2));
    }

    @Test
    void testPatchUpdateSuccessfully() {
        Tasks task = new Tasks(null, "Original Task", TaskPriority.LOW, false, LocalDate.now());
        taskRepository.create(task);

        Tasks updateData = new Tasks(null, "Updated Task", TaskPriority.HIGH, true, LocalDate.now());
        Tasks updatedTask = taskRepository.patchUpdate(1, updateData);

        assertEquals("Updated Task", updatedTask.getTaskName());
        assertEquals(TaskPriority.HIGH, updatedTask.getTaskPriority());
        assertTrue(updatedTask.getCompleted());
    }

    @Test
    void testPatchUpdateThrowsExceptionIfNotFound() {
        Tasks updateData = new Tasks(null, "Updated Task", TaskPriority.HIGH, true, LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> taskRepository.patchUpdate(999, updateData));
    }

    @Test
    void testMarkAsUnDoneSuccessfully() {
        Tasks task = new Tasks(null, "Task to Uncomplete", TaskPriority.MEDIUM, true, LocalDate.now());
        taskRepository.create(task);

        Optional<Tasks> markedTask = taskRepository.markAsUnDone(1);

        assertTrue(markedTask.isPresent());
        assertFalse(markedTask.get().getCompleted());
    }

    @Test
    void testMarkAsUnDoneNotFound() {
        Optional<Tasks> markedTask = taskRepository.markAsUnDone(999);
        assertTrue(markedTask.isEmpty());
    }

    @Test
    void testGetAverageCompletionTime() {
        Tasks task1 = new Tasks(null, "Task 1", TaskPriority.HIGH, false, LocalDate.now().minusDays(1));
        Tasks task2 = new Tasks(null, "Task 2", TaskPriority.LOW, false, LocalDate.now());

        taskRepository.create(task1);
        taskRepository.create(task2);
        taskRepository.markAsDone(task1.getId());
        taskRepository.markAsDone(task2.getId());

        double avgTime = taskRepository.getAverageCompletionTime();
        assertTrue(avgTime >= 0);
    }

    @Test
    void testGetAverageCompletionTimeNoCompletedTasks() {
        Tasks task = new Tasks(null, "Task 1", TaskPriority.HIGH, false, LocalDate.now());
        taskRepository.create(task);

        double avgTime = taskRepository.getAverageCompletionTime();
        assertEquals(0.0, avgTime);
    }

    @Test
    void testDeleteTaskSuccessfully() {
        Tasks task = new Tasks(null, "Task to Delete", TaskPriority.LOW, false, LocalDate.now());
        taskRepository.create(task);

        boolean deleted = taskRepository.delete(1);
        assertTrue(deleted);
        assertTrue(taskRepository.findById(1).isEmpty());
    }

    @Test
    void testDeleteTaskNotFound() {
        boolean deleted = taskRepository.delete(999);
        assertFalse(deleted);
    }

    @Test
    void testFindAllFiltersAndSorts() {
        Tasks task1 = new Tasks(null, "Task A", TaskPriority.HIGH, true, LocalDate.now().plusDays(3));
        Tasks task2 = new Tasks(null, "Task B", TaskPriority.MEDIUM, false, LocalDate.now().plusDays(1));
        Tasks task3 = new Tasks(null, "Task C", TaskPriority.LOW, true, LocalDate.now().plusDays(2));

        taskRepository.create(task1);
        taskRepository.create(task2);
        taskRepository.create(task3);

        List<Tasks> tasks = taskRepository.findAll(0, 2, "taskDueDate", null, null, null, null);

        assertEquals(2, tasks.size());
        assertEquals("Task B", tasks.get(0).getTaskName());
        assertEquals("Task C", tasks.get(1).getTaskName());
    }
}
