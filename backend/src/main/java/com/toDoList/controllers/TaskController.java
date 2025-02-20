package com.toDoList.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.toDoList.TaskPriority;
import com.toDoList.models.Tasks;
import com.toDoList.services.TaskRepository;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/todos")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("")
    List<Tasks> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String taskName) {
        return taskRepository.findAll(page, size, sortBy, filterBy, priority, completed, taskName);
    }

    @GetMapping("/{id}")
    Tasks findById(@PathVariable Integer id) throws Exception {

        Optional<Tasks> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            throw new Exception();
        }
        return task.get();
    }

    // get4 time general
    @GetMapping("/avg-done-time")
    public double getAverageCompletionTime() {
        return taskRepository.getAverageCompletionTime();
    }

    // get4 time per prior
    @GetMapping("/avg-done-time-priorities")
    public Map<TaskPriority, Double> getAverageCompletionTimePerPriority() {
        return taskRepository.getAverageCompletionTimePerPriority();
    }

    // post
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    void create(@Valid @RequestBody Tasks task) {
        taskRepository.create(task);
    }

    // patch to updt completed
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/done")
    void markTaskAsDone(@PathVariable Integer id) throws Exception {
        taskRepository.markAsDone(id)
                .orElseThrow(() -> new Exception("ToDo not found"));
    }

    // pathc to updt uncompleted
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/undone")
    void markTaskAsUnDone(@PathVariable Integer id) throws Exception {
        taskRepository.markAsUnDone(id)
                .orElseThrow(() -> new Exception("ToDo not found"));
    }

    // put
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    void update(@Valid @RequestBody Tasks task, @PathVariable Integer id) {
        taskRepository.patchUpdate(id, task);
    }

    // delete
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        taskRepository.delete(id);
    }

}
