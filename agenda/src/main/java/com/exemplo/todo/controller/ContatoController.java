package com.exemplo.todo.controller;

import com.exemplo.todo.entity.Contato;
import com.exemplo.todo.service.ContatoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/contatos")
public class ContatoController {

    private final ContatoService service;

    public ContatoController(ContatoService service) {
        this.service = service;
    }

    @PostMapping
    public Contato criar(@RequestBody Contato contato) {
        return service.criar(contato);
    }

    @GetMapping
    public List<Contato> listar() {
        return service.listarTodos();
    }

// ContatoController.java
    @PutMapping("/{id}")
    public Contato atualizar(@PathVariable Long id, @RequestBody Contato contato) {
        return service.atualizar(id, contato);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}