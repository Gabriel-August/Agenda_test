package com.exemplo.todo.service;

import com.exemplo.todo.entity.Task;
import com.exemplo.todo.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository repository;

    @Test
    @DisplayName("Deve salvar e encontrar uma task por ID")
    void deveSalvarEEncontrarTaskPorId() {
        // Cenário
        Task novaTask = new Task();
        novaTask.setTitulo("Aprender Testes de Integração");
        Task taskSalva = entityManager.persistAndFlush(novaTask);

        // Ação
        Optional<Task> taskEncontrada = repository.findById(taskSalva.getId());

        // Verificação
        assertTrue(taskEncontrada.isPresent(), "A task deveria ser encontrada no banco de dados");
        assertEquals(taskSalva.getTitulo(), taskEncontrada.get().getTitulo(), "O título da task encontrada não corresponde ao esperado");
    }

    @Test
    @DisplayName("Deve deletar uma task com sucesso")
    void deveDeletarTask() {
        // Cenário
        Task novaTask = new Task();
        novaTask.setTitulo("Task para deletar");
        Task taskSalva = entityManager.persistAndFlush(novaTask);
        Long id = taskSalva.getId();

        // Ação
        repository.deleteById(id);
        Optional<Task> taskDeletada = repository.findById(id);

        // Verificação
        assertFalse(taskDeletada.isPresent(), "A task não deveria mais existir no banco de dados após ser deletada");
    }
}