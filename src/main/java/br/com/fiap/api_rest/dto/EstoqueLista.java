package br.com.fiap.api_rest.dto;

import org.springframework.hateoas.Link;

import java.util.UUID;

public record EstoqueLista(UUID id, String produtoNome, int quantidade, Link link) {
}
