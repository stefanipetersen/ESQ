package br.com.fiap.esq.controller;

import br.com.fiap.esq.dto.PerfilRequest;
import br.com.fiap.esq.dto.SenhaRequest;
import br.com.fiap.esq.model.Usuario;
import br.com.fiap.esq.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

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

    //200 OK, 404 Not Found ou 422 (e-mail já usado)
    @PutMapping("/{id}/perfil")
    public Usuario atualizarPerfil(@PathVariable int id, @RequestBody @Valid PerfilRequest body) {
        return service.atualizarPerfil(id, body);
    }

    //200 OK com mensagem, 404 Not Found ou 422 (senha atual incorreta)
    @PutMapping("/{id}/senha")
    public Map<String, String> alterarSenha(@PathVariable int id, @RequestBody @Valid SenhaRequest body) {
        service.alterarSenha(id, body);
        return Map.of("mensagem", "Senha alterada com sucesso");
    }

    //200 OK com mensagem ou 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable int id) {
        service.deletar(id);
        return ResponseEntity.ok(Map.of("mensagem", "Usuário deletado com sucesso"));
    }
}
