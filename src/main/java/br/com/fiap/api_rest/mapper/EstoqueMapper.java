package br.com.fiap.api_rest.mapper;

import br.com.fiap.api_rest.controller.EstoqueController;
import br.com.fiap.api_rest.dto.EstoqueLista;
import br.com.fiap.api_rest.dto.EstoqueResponse;
import br.com.fiap.api_rest.model.Estoque;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EstoqueMapper {

    public EstoqueResponse estoqueToResponse(Estoque estoque) {
        Link link = linkTo(methodOn(EstoqueController.class).readEstoques(0)).withRel("Lista de estoques");
        return new EstoqueResponse(estoque.getId(),
                estoque.getProduto() == null ? null : estoque.getProduto().getId(),
                estoque.getProduto() == null ? null : estoque.getProduto().getNome(),
                estoque.getQuantidade(),
                estoque.getQuantidadeMinima(),
                link);
    }

    public EstoqueLista estoqueToEstoqueLista(Estoque estoque) {
        Link link = linkTo(methodOn(EstoqueController.class).readEstoque(estoque.getId()))
                .withRel("Detalhes do estoque");
        return new EstoqueLista(estoque.getId(),
                estoque.getProduto() == null ? null : estoque.getProduto().getNome(),
                estoque.getQuantidade(),
                link);
    }
}
