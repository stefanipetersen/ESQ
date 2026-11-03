package br.com.fiap.esq.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "E-mail é obrigatório") String dsEmail,
        @NotBlank(message = "Senha é obrigatória") String senha) {
}
