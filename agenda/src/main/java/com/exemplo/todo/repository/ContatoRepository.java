package com.exemplo.todo.repository;

import com.exemplo.todo.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

    boolean existsByNomeAndTelefone(String nome, String telefone);

    List<Contato> findByNomeContainingIgnoreCase(String nome);

    List<Contato> findByEmailContainingIgnoreCase(String email);
}
