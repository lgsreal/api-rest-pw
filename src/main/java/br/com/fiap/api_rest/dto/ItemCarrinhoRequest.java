package br.com.fiap.api_rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemCarrinhoRequest(
        @NotNull(message = "O id do produto \u00e9 obrigat\u00f3rio")
        UUID produtoId,
        @NotNull(message = "A quantidade \u00e9 obrigat\u00f3ria")
        @Min(value = 1, message = "A quantidade m\u00ednima \u00e9 1")
        Integer quantidade
) {
}
