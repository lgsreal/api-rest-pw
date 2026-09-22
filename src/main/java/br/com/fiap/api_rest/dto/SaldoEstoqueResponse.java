package br.com.fiap.api_rest.dto;

import java.util.UUID;

public record SaldoEstoqueResponse(UUID estoqueId, UUID produtoId, UUID depositoId, int quantidade) {
}
