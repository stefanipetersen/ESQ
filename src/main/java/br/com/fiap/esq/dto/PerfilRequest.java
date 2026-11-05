package br.com.fiap.esq.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

//Dados do perfil que o usuário edita nas configurações (sem mexer na senha)
public record PerfilRequest(
        @NotBlank(message = "Nome é obrigatório") String nmUsuario,
        @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String dsEmail,
        String nrTelefone,
        LocalDate dtNascimento) {
}
