package br.com.fiap.api_rest.mapper;

import br.com.fiap.api_rest.controller.ProdutoController;
import br.com.fiap.api_rest.dto.ProdutoLista;
import br.com.fiap.api_rest.dto.ProdutoResponse;
import br.com.fiap.api_rest.model.Produto;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProdutoMapper {
    public ProdutoResponse produtoToResponse(Produto produto) {
        // Link para a lista de produtos, ou seja, read(pageable) do controller
        Link link = linkTo(methodOn(ProdutoController.class).readProduto(0)).withRel("Lista de produtos");
        return new ProdutoResponse(produto.getId(), produto.getNome(), produto.getPreco(), link);
    }

    public ProdutoLista produtoToProdutoLista(Produto produto) {
        // Link para ele mesmo, para o produto pelo id
        Link link = linkTo(methodOn(ProdutoController.class).readProduto(produto.getId())).withRel("Detalhes do produto");
        return new ProdutoLista(produto.getNome(), link);
    }
}
