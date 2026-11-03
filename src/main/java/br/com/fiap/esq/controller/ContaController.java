package br.com.fiap.esq.controller;

import br.com.fiap.esq.model.Conta;
import br.com.fiap.esq.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    //200 OK
    @GetMapping
    public List<Conta> listar() {
        return service.listar();
    }

    //200 OK ou 404 Not Found
    @GetMapping("/{id}")
    public Conta buscar(@PathVariable int id) {
        return service.buscar(id);
    }

    //201 Created com o header Location
    @PostMapping
    public ResponseEntity<Conta> cadastrar(@RequestBody @Valid Conta body) {
        Conta criada = service.cadastrar(body);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criada.getIdConta()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    //200 OK ou 404 Not Found
    @PutMapping("/{id}")
    public Conta atualizar(@PathVariable int id, @RequestBody @Valid Conta body) {
        return service.atualizar(id, body);
    }

    //204 No Content ou 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
