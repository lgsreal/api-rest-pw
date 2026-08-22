package br.com.fiap.api_rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CarrinhoRequest(
        @NotNull(message = "O id do usu\u00e1rio \u00e9 obrigat\u00f3rio")
        UUID usuarioId,
        @NotEmpty(message = "O carrinho deve ter ao menos um item")
        @Valid
        List<ItemCarrinhoRequest> itens
) {
}
