package br.com.fiap.esq.controller;

import br.com.fiap.esq.model.Meta;
import br.com.fiap.esq.service.MetaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/metas")
public class MetaController {

    private final MetaService service;

    public MetaController(MetaService service) {
        this.service = service;
    }

    //200 OK (com ?idUsuario= lista só as do usuário)
    @GetMapping
    public List<Meta> listar(@RequestParam(required = false) Integer idUsuario) {
        return service.listar(idUsuario);
    }

    //200 OK ou 404 Not Found
    @GetMapping("/{id}")
    public Meta buscar(@PathVariable int id) {
        return service.buscar(id);
    }

    //201 Created com o header Location
    @PostMapping
    public ResponseEntity<Meta> cadastrar(@RequestBody @Valid Meta body) {
        Meta criada = service.cadastrar(body);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criada.getIdMeta()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    //200 OK ou 404 Not Found
    @PutMapping("/{id}")
    public Meta atualizar(@PathVariable int id, @RequestBody @Valid Meta body) {
        return service.atualizar(id, body);
    }

    //200 OK com mensagem ou 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable int id) {
        service.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Meta deletada com sucesso"));
    }
}
