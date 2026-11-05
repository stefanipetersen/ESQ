package br.com.fiap.esq.controller;

import br.com.fiap.esq.model.Configuracao;
import br.com.fiap.esq.service.ConfiguracaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/configuracoes")
public class ConfiguracaoController {

    private final ConfiguracaoService service;

    public ConfiguracaoController(ConfiguracaoService service) {
        this.service = service;
    }

    //200 OK (com ?idUsuario= lista só a do usuário)
    @GetMapping
    public List<Configuracao> listar(@RequestParam(required = false) Integer idUsuario) {
        return service.listar(idUsuario);
    }

    //200 OK ou 404 Not Found
    @GetMapping("/{id}")
    public Configuracao buscar(@PathVariable int id) {
        return service.buscar(id);
    }

    //201 Created com o header Location
    @PostMapping
    public ResponseEntity<Configuracao> cadastrar(@RequestBody @Valid Configuracao body) {
        Configuracao criada = service.cadastrar(body);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criada.getIdConfiguracao()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    //200 OK ou 404 Not Found
    @PutMapping("/{id}")
    public Configuracao atualizar(@PathVariable int id, @RequestBody @Valid Configuracao body) {
        return service.atualizar(id, body);
    }

    //200 OK com mensagem ou 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable int id) {
        service.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Configuração deletada com sucesso"));
    }
}
