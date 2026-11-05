package br.com.fiap.esq.controller;

import br.com.fiap.esq.model.Transacao;
import br.com.fiap.esq.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    //200 OK (filtros opcionais: ?idUsuario= e ?tipo=RECEITA|DESPESA)
    @GetMapping
    public List<Transacao> listar(@RequestParam(required = false) Integer idUsuario,
                                  @RequestParam(required = false) String tipo) {
        return service.listar(idUsuario, tipo);
    }

    //200 OK ou 404 Not Found
    @GetMapping("/{id}")
    public Transacao buscar(@PathVariable int id) {
        return service.buscar(id);
    }

    //201 Created com o header Location
    @PostMapping
    public ResponseEntity<Transacao> cadastrar(@RequestBody @Valid Transacao body) {
        Transacao criada = service.cadastrar(body);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criada.getIdTransacao()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    //200 OK ou 404 Not Found (com ?proximas=true muda também as ocorrências dos meses seguintes)
    @PutMapping("/{id}")
    public Transacao atualizar(@PathVariable int id, @RequestBody @Valid Transacao body,
                               @RequestParam(defaultValue = "false") boolean proximas) {
        return service.atualizar(id, body, proximas);
    }

    //200 OK com mensagem ou 404 Not Found (com ?proximas=true apaga também as parcelas / meses seguintes)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable int id,
                                                       @RequestParam(defaultValue = "false") boolean proximas) {
        service.deletar(id, proximas);
        return ResponseEntity.ok(Map.of("mensagem",
                proximas ? "Transação e as dos próximos meses deletadas com sucesso" : "Transação deletada com sucesso"));
    }
}
