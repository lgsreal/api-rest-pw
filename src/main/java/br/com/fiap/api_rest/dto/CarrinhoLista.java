package br.com.fiap.api_rest.dto;

import org.springframework.hateoas.Link;

import java.math.BigDecimal;
import java.util.UUID;

public record CarrinhoLista(UUID id, int quantidadeItens, BigDecimal total, Link link) {
}
