package br.com.fiap.api_rest.dto;

import br.com.fiap.api_rest.model.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record MovimentacaoRequest(
        @NotNull(message = "O produto é obrigatório")
        UUID produtoId,
        @NotNull(message = "O depósito é obrigatório")
        UUID depositoId,
        @NotNull(message = "O tipo da movimentação é obrigatório")
        TipoMovimentacao tipo,
        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade
) {
}
