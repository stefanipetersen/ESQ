package br.com.fiap.esq.controller;

import br.com.fiap.esq.model.Investimento;
import br.com.fiap.esq.service.InvestimentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/investimentos")
public class InvestimentoController {

    private final InvestimentoService service;

    public InvestimentoController(InvestimentoService service) {
        this.service = service;
    }

    //200 OK
    @GetMapping
    public List<Investimento> listar() {
        return service.listar();
    }

    //200 OK ou 404 Not Found
    @GetMapping("/{id}")
    public Investimento buscar(@PathVariable int id) {
        return service.buscar(id);
    }

    //201 Created com o header Location
    @PostMapping
    public ResponseEntity<Investimento> cadastrar(@RequestBody @Valid Investimento body) {
        Investimento criado = service.cadastrar(body);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criado.getIdInvestimento()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    //200 OK ou 404 Not Found
    @PutMapping("/{id}")
    public Investimento atualizar(@PathVariable int id, @RequestBody @Valid Investimento body) {
        return service.atualizar(id, body);
    }

    //200 OK com mensagem ou 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable int id) {
        service.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Investimento deletado com sucesso"));
    }
}
