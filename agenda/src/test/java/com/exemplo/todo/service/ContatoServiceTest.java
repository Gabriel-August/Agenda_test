//Biel atualizações
package com.exemplo.todo.service;

import com.exemplo.todo.entity.Contato;
import com.exemplo.todo.repository.ContatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContatoServiceTest {

    @Mock
    private ContatoRepository repository;

    @InjectMocks
    private ContatoService service;

    private Contato contato;

    @BeforeEach
    void setUp() {
        // Objeto padrão para uso nos testes
        contato = new Contato(1L, "Bruce Wayne", "99999-0101", "bruce@wayneenterprises.com");
    }

    // --- Testes para o método criar() ---

    @Test
    @DisplayName("Deve criar um novo contato com sucesso")
    void deveCriarContatoComSucesso() {
        // Arrange
        when(repository.existsByNomeAndTelefone(contato.getNome(), contato.getTelefone())).thenReturn(false);
        when(repository.save(contato)).thenReturn(contato);

        // Act
        Contato contatoSalvo = service.criar(contato);

        // Assert
        assertNotNull(contatoSalvo);
        assertEquals("Bruce Wayne", contatoSalvo.getNome());
        verify(repository).save(contato);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar um contato duplicado")
    void deveLancarExcecaoAoCriarContatoDuplicado() {
        // Arrange
        when(repository.existsByNomeAndTelefone(contato.getNome(), contato.getTelefone())).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.criar(contato);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Contato com este nome e telefone já existe.", exception.getReason());
        verify(repository, never()).save(any(Contato.class));
    }

    // --- Testes para o método listarTodos() ---

    @Test
    @DisplayName("Deve listar todos os contatos existentes")
    void deveListarTodosOsContatos() {
        // Arrange
        List<Contato> contatos = Arrays.asList(
                contato,
                new Contato(2L, "Clark Kent", "88888-0202", "clark@dailyplanet.com")
        );
        when(repository.findAll()).thenReturn(contatos);

        // Act
        List<Contato> resultado = service.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver contatos")
    void deveRetornarListaVaziaQuandoNaoHouverContatos() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Contato> resultado = service.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // --- Testes para o método buscarPorNomeOuEmail() ---

    @Test
    @DisplayName("Deve buscar contatos por nome")
    void deveBuscarContatosPorNome() {
        // Arrange
        when(repository.findByNomeContainingIgnoreCase("Bruce")).thenReturn(List.of(contato));

        // Act
        List<Contato> resultado = service.buscarPorNomeOuEmail("Bruce", null);

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals("Bruce Wayne", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar contatos por email")
    void deveBuscarContatosPorEmail() {
        // Arrange
        when(repository.findByEmailContainingIgnoreCase("wayne")).thenReturn(List.of(contato));

        // Act
        List<Contato> resultado = service.buscarPorNomeOuEmail(null, "wayne");

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals("bruce@wayneenterprises.com", resultado.get(0).getEmail());
    }

    // --- Testes para o método atualizar() ---

    @Test
    @DisplayName("Deve atualizar um contato existente com sucesso")
    void deveAtualizarContatoComSucesso() {
        // Arrange
        Contato contatoAtualizado = new Contato(1L, "Batman", "11111-1111", "batman@wayneenterprises.com");
        when(repository.findById(1L)).thenReturn(Optional.of(contato));
        when(repository.save(any(Contato.class))).thenReturn(contatoAtualizado);

        // Act
        Contato resultado = service.atualizar(1L, contatoAtualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Batman", resultado.getNome());
        assertEquals("11111-1111", resultado.getTelefone());
        verify(repository).save(contato);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um contato inexistente")
    void deveLancarExcecaoAoAtualizarContatoInexistente() {
        // Arrange
        Contato contatoAtualizado = new Contato(99L, "Inexistente", "00000-0000", null);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            service.atualizar(99L, contatoAtualizado);
        });
    }

    // --- Testes para o método excluir() ---

    @Test
    @DisplayName("Deve excluir um contato existente com sucesso")
    void deveExcluirContatoComSucesso() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        // Act
        service.excluir(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir um contato inexistente")
    void deveLancarExcecaoAoExcluirContatoInexistente() {
        // Arrange
        when(repository.existsById(99L)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.excluir(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(repository, never()).deleteById(anyLong());
    }
}