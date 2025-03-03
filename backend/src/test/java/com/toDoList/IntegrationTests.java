package com.toDoList;

import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toDoList.models.Tasks;
import com.toDoList.services.TaskRepository;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests {

    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper; 

    @BeforeEach
    void setUp() {
        taskRepository.init();
    }

    @Test
    void givenTasks_whenGetAll_thenReturnTasksList() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @Test
    void givenTask_whenCreate_thenTaskIsCreated() throws Exception {
        Tasks task = new Tasks(null, "New Integration Task", TaskPriority.MEDIUM, false, LocalDate.now());

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());
    }

    @Test
    void givenExistingTask_whenGetById_thenReturnTask() throws Exception {
        Tasks task = new Tasks(null, "Find Me", TaskPriority.LOW, false, LocalDate.now());
        taskRepository.create(task);

        mockMvc.perform(get("/todos/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskName").value("Find Me"));
    }

    @Test
    void givenNonExistingTask_whenGetById_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/todos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenExistingTask_whenDelete_thenTaskIsDeleted() throws Exception {
        Tasks task = new Tasks(null, "Delete Me", TaskPriority.HIGH, false, LocalDate.now());
        taskRepository.create(task);

        mockMvc.perform(delete("/todos/" + task.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/todos/" + task.getId()))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void givenTask_whenMarkAsDone_thenTaskIsUpdated() throws Exception {
        Tasks task = new Tasks(null, "Complete Me", TaskPriority.HIGH, false, LocalDate.now());
        taskRepository.create(task);

        mockMvc.perform(patch("/todos/" + task.getId() + "/done"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/todos/" + task.getId()))
                .andExpect(jsonPath("$.completed").value(true)); 
    }
}
