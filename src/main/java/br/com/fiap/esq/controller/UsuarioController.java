package br.com.fiap.esq.controller;

import br.com.fiap.esq.model.Usuario;
import br.com.fiap.esq.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    //200 OK
    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    //200 OK ou 404 Not Found
    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable int id) {
        return service.buscar(id);
    }

    //201 Created com o header Location
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody @Valid Usuario body) {
        Usuario criado = service.cadastrar(body);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criado.getIdUsuario()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    //200 OK ou 404 Not Found
    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable int id, @RequestBody @Valid Usuario body) {
        return service.atualizar(id, body);
    }

    //204 No Content ou 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
