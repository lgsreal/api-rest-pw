package br.com.fiap.api_rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EstoqueRequest(
        @NotNull(message = "O id do produto \u00e9 obrigat\u00f3rio")
        UUID produtoId,
        @NotNull(message = "A quantidade \u00e9 obrigat\u00f3ria")
        @Min(value = 0, message = "A quantidade n\u00e3o pode ser negativa")
        Integer quantidade,
        @Min(value = 0, message = "A quantidade m\u00ednima n\u00e3o pode ser negativa")
        Integer quantidadeMinima
) {
}
