package com.exemplo.todo.service;

import com.exemplo.todo.entity.Task;
import com.exemplo.todo.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @BeforeEach
    void setup() {
        // Inicia os mocks antes de cada teste
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar uma nova task com sucesso")
    void deveCriarUmaTask() {
        // Cenário
        Task task = new Task(1L, "Estudar Spring", false);
        // Mock do repositório para simular o salvamento
        when(repository.save(any(Task.class))).thenReturn(task);

        // Ação
        Task criada = service.criarTask("Estudar Spring");

        // Verificação
        assertNotNull(criada, "A task criada não deveria ser nula");
        assertEquals("Estudar Spring", criada.getTitulo(), "O título da task não corresponde ao esperado");
        // Verifica se o método save foi chamado exatamente 1 vez
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve listar todas as tasks")
    void deveListarTodasAsTasks() {
        // Cenário
        List<Task> tasks = Arrays.asList(
                new Task(1L, "Estudar Docker", false),
                new Task(2L, "Fazer café", true)
        );
        when(repository.findAll()).thenReturn(tasks);

        // Ação
        List<Task> resultado = service.listarTodas();

        // Verificação
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "O tamanho da lista de tasks é diferente do esperado");
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver tasks")
    void deveRetornarListaVazia() {
        // Cenário
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Ação
        List<Task> resultado = service.listarTodas();

        // Verificação
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty(), "A lista deveria estar vazia");
        verify(repository, times(1)).findAll();
    }


    @Test
    @DisplayName("Deve marcar uma task como concluída com sucesso")
    void deveMarcarTaskComoConcluida() {
        // Cenário
        Task task = new Task(1L, "Estudar Spring", false);
        // Simula a busca da task pelo ID
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        // Simula o salvamento da task já atualizada
        when(repository.save(any(Task.class))).thenAnswer(invocation -> {
            Task taskSalva = invocation.getArgument(0);
            taskSalva.setConcluida(true);
            return taskSalva;
        });

        // Ação
        Task concluida = service.marcarComoConcluida(1L);

        // Verificação
        assertTrue(concluida.isConcluida(), "A task deveria estar marcada como concluída");
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar marcar como concluída uma task que não existe")
    void deveLancarExcecaoQuandoTaskNaoEncontrada() {
        // Cenário
        long idInexistente = 99L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        // Ação e Verificação
        // Verifica se a exceção RuntimeException é lançada
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.marcarComoConcluida(idInexistente);
        });

        assertEquals("Tarefa não encontrada", exception.getMessage());
        verify(repository, times(1)).findById(idInexistente);
        // Garante que o método save nunca seja chamado
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve excluir uma task com sucesso")
    void deveExcluirTask() {
        // Cenário
        Long taskId = 1L;
        // O método deleteById retorna void, então podemos usar doNothing()
        doNothing().when(repository).deleteById(taskId);

        // Ação
        // Chama o método a ser testado
        service.excluirTask(taskId);

        // Verificação
        // Confirma que o método deleteById foi chamado com o ID correto
        verify(repository, times(1)).deleteById(taskId);
    }
}