package br.com.fiap.api_rest.dto;

import org.springframework.hateoas.Link;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CarrinhoResponse(UUID id, UUID usuarioId, List<ItemCarrinhoResponse> itens,
                               BigDecimal total, Link link) {
}
