package br.com.fiap.api_rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepositoRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
        String nome,
        String localizacao
) {
}
