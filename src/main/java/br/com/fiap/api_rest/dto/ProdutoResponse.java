package br.com.fiap.api_rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoResponse(UUID id, String nome, BigDecimal preco) {
}
