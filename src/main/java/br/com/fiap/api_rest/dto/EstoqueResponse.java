package br.com.fiap.api_rest.dto;

import org.springframework.hateoas.Link;

import java.util.UUID;

public record EstoqueResponse(UUID id, UUID produtoId, String produtoNome, int quantidade,
                              int quantidadeMinima, Link link) {
}
