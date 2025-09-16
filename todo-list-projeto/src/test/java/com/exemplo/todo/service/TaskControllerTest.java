package com.exemplo.todo.service;

import com.exemplo.todo.entity.Task;
import com.exemplo.todo.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    // Limpa o banco de dados após cada teste para garantir o isolamento
    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void deveCriarTaskViaEndpointPost() throws Exception {
        mockMvc.perform(post("/tasks")
                        .param("titulo", "Nova Task via API")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.titulo", is("Nova Task via API")))
                .andExpect(jsonPath("$.concluida", is(false)));
    }

    @Test
    void deveListarTasksViaEndpointGet() throws Exception {
        // Cenário
        repository.save(new Task(null, "Task 1", false));
        repository.save(new Task(null, "Task 2", true));

        // Ação e Verificação
        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo", is("Task 1")))
                .andExpect(jsonPath("$[1].titulo", is("Task 2")));
    }

    @Test
    void deveMarcarTaskComoConcluidaViaEndpointPut() throws Exception {
        // Cenário
        Task task = repository.save(new Task(null, "A fazer", false));
        Long taskId = task.getId();

        // Ação e Verificação
        mockMvc.perform(put("/tasks/{id}/concluir", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.concluida", is(true)));
    }

    @Test
    void deveExcluirTaskViaEndpointDelete() throws Exception {
        // Cenário
        Task task = repository.save(new Task(null, "Para excluir", false));
        Long taskId = task.getId();

        // Ação e Verificação
        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isOk());

        // Verifica se a task foi realmente removida
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}