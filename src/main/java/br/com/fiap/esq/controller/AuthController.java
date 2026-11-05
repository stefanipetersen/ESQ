package br.com.fiap.esq.controller;

import br.com.fiap.esq.dto.LoginRequest;
import br.com.fiap.esq.model.Usuario;
import br.com.fiap.esq.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //200 OK com o usuário ou 401 Unauthorized
    @PostMapping("/login")
    public Usuario login(@RequestBody @Valid LoginRequest body) {
        return usuarioService.autenticar(body.dsEmail(), body.senha());
    }
}
