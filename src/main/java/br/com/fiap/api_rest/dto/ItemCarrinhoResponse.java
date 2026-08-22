package br.com.fiap.api_rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemCarrinhoResponse(UUID id, UUID produtoId, String produtoNome, int quantidade,
                                   BigDecimal precoUnitario, BigDecimal subtotal) {
}
