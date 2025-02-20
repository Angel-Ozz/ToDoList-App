package com.toDoList.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toDoList.TaskPriority;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty; //javax dsnt work with spring
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Tasks {
    @Id
    private Integer id;
    @NotEmpty
    @Size(max = 120)
    private String taskName;
    @NotNull
    private TaskPriority taskPriority;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // USed for deserialization protection, because it was always
                                                          // setting it to null
    private LocalDateTime creationDate = LocalDateTime.now();
    @NotNull
    private Boolean completed;
    @FutureOrPresent
    private LocalDate taskDueDate;
    @FutureOrPresent
    private LocalDateTime doneDate;

    // this bcause json serialization and des
    public Tasks() {
    }

    // constr with dueDate
    public Tasks(Integer id, String taskName, TaskPriority taskPriority, Boolean completed, LocalDate taskDate) {
        this.id = id;
        this.taskName = taskName;
        this.taskPriority = taskPriority;
        this.creationDate = LocalDateTime.now();
        this.completed = completed;
        this.taskDueDate = taskDate;

    }

    // constr wthout dueDate
    public Tasks(Integer id, String taskName, TaskPriority taskPriority, Boolean completed) {
        this.id = id;
        this.taskName = taskName;
        this.taskPriority = taskPriority;
        this.creationDate = LocalDateTime.now();
        this.completed = completed;
        this.taskDueDate = null;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        if (completed != null && completed && (this.completed == null || !this.completed)) {
            this.completed = true;
            this.doneDate = LocalDateTime.now();
        } else if (completed != null && !completed) {
            this.completed = false;
            this.doneDate = null;
        }
    }

    public LocalDate getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(LocalDate taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public LocalDateTime getDoneDate() {
        return doneDate;
    }

}
