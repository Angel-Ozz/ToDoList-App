package com.toDoList.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     public ResponseEntity<List<Tasks>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String taskName) {
        List<Tasks> tasks = taskRepository.findAll(page, size, sortBy, filterBy, priority, completed, taskName);
        if (tasks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tasks> findById(@PathVariable Integer id) {
        Optional<Tasks> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(task.get(), HttpStatus.OK);
    }

    // get4 time general
    @GetMapping("/avg-done-time")
    public ResponseEntity<Double> getAverageCompletionTime() {
        double avgTime = taskRepository.getAverageCompletionTime();
        return new ResponseEntity<>(avgTime, HttpStatus.OK);
    }

    // get4 time per prior
    @GetMapping("/avg-done-time-priorities")
    public ResponseEntity<Map<TaskPriority, Double>> getAverageCompletionTimePerPriority() {
        Map<TaskPriority, Double> avgTimes = taskRepository.getAverageCompletionTimePerPriority();
        return new ResponseEntity<>(avgTimes, HttpStatus.OK);
    }

    // post
    @PostMapping("")
    public ResponseEntity<Void> create(@Valid @RequestBody Tasks task) {
        taskRepository.create(task);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // patch to updt completed
    @PatchMapping("/{id}/done")
    public ResponseEntity<Void> markTaskAsDone(@PathVariable Integer id) {
        boolean updated = taskRepository.markAsDone(id).isPresent();
        if (!updated) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // pathc to updt uncompleted
    @PatchMapping("/{id}/undone")
    public ResponseEntity<Void> markTaskAsUnDone(@PathVariable Integer id) {
        boolean updated = taskRepository.markAsUnDone(id).isPresent();
        if (!updated) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // put
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody Tasks task, @PathVariable Integer id) {
        taskRepository.patchUpdate(id, task);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // delete
    @DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Integer id) {
    taskRepository.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
