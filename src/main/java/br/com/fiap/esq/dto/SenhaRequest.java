package br.com.fiap.esq.dto;

import jakarta.validation.constraints.NotBlank;

public record SenhaRequest(
        @NotBlank(message = "Senha atual é obrigatória") String senhaAtual,
        @NotBlank(message = "Nova senha é obrigatória") String novaSenha) {
}
