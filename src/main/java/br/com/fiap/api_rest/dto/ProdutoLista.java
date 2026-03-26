package br.com.fiap.api_rest.dto;

import org.springframework.hateoas.Link;

public record ProdutoLista(String nome, Link link) {
}
