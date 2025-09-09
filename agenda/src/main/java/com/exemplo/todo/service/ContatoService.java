package com.exemplo.todo.service;

import com.exemplo.todo.entity.Contato;
import com.exemplo.todo.repository.ContatoRepository;
import org.springframework.stereotype.Service;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
public class ContatoService {

    private final ContatoRepository repository;

    public ContatoService(ContatoRepository repository) {
        this.repository = repository;
    }

    // Criar um novo contato, verificando duplicatas
    public Contato criar(@Valid Contato contato) {
        if (repository.existsByNomeAndTelefone(contato.getNome(), contato.getTelefone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Contato com este nome e telefone já existe.");
        }
        return repository.save(contato);
    }

    // Listar todos os contatos
    public List<Contato> listarTodos() {
        return repository.findAll();
    }

    // Listar contatos por nome ou email (novo requisito)
    public List<Contato> buscarPorNomeOuEmail(String nome, String email) {
        if (nome != null && !nome.isEmpty()) {
            return repository.findByNomeContainingIgnoreCase(nome);
        } else if (email != null && !email.isEmpty()) {
            return repository.findByEmailContainingIgnoreCase(email);
        }
        return listarTodos();
    }

    // Atualizar um contato existente
    public Contato atualizar(Long id, @Valid Contato contatoAtualizado) {
        return repository.findById(id).map(contato -> {
            contato.setNome(contatoAtualizado.getNome());
            contato.setTelefone(contatoAtualizado.getTelefone());
            contato.setEmail(contatoAtualizado.getEmail());
            return repository.save(contato);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado."));
    }

    // Excluir um contato por ID
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contato não encontrado.");
        }
        repository.deleteById(id);
    }
}