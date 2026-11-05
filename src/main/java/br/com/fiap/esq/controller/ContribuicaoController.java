package br.com.fiap.esq.controller;

import br.com.fiap.esq.model.Contribuicao;
import br.com.fiap.esq.service.ContribuicaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contribuicoes")
public class ContribuicaoController {

    private final ContribuicaoService service;

    public ContribuicaoController(ContribuicaoService service) {
        this.service = service;
    }

    //200 OK (filtros opcionais: ?idMeta= ou ?idUsuario=)
    @GetMapping
    public List<Contribuicao> listar(@RequestParam(required = false) Integer idMeta,
                                     @RequestParam(required = false) Integer idUsuario) {
        return service.listar(idMeta, idUsuario);
    }

    //200 OK ou 404 Not Found
    @GetMapping("/{id}")
    public Contribuicao buscar(@PathVariable int id) {
        return service.buscar(id);
    }

    //201 Created com o header Location
    @PostMapping
    public ResponseEntity<Contribuicao> cadastrar(@RequestBody @Valid Contribuicao body) {
        Contribuicao criada = service.cadastrar(body);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criada.getIdContribuicao()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    //200 OK ou 404 Not Found
    @PutMapping("/{id}")
    public Contribuicao atualizar(@PathVariable int id, @RequestBody @Valid Contribuicao body) {
        return service.atualizar(id, body);
    }

    //200 OK com mensagem ou 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable int id) {
        service.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Contribuição deletada com sucesso"));
    }
}
