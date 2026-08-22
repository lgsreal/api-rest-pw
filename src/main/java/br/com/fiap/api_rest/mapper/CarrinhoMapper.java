package br.com.fiap.api_rest.mapper;

import br.com.fiap.api_rest.controller.CarrinhoController;
import br.com.fiap.api_rest.dto.CarrinhoLista;
import br.com.fiap.api_rest.dto.CarrinhoResponse;
import br.com.fiap.api_rest.dto.ItemCarrinhoResponse;
import br.com.fiap.api_rest.model.Carrinho;
import br.com.fiap.api_rest.model.ItemCarrinho;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CarrinhoMapper {

    public CarrinhoResponse carrinhoToResponse(Carrinho carrinho) {
        Link link = linkTo(methodOn(CarrinhoController.class).readCarrinhos(0)).withRel("Lista de carrinhos");
        List<ItemCarrinhoResponse> itens = carrinho.getItens().stream()
                .map(this::itemToResponse)
                .toList();
        return new CarrinhoResponse(carrinho.getId(),
                carrinho.getUsuario() == null ? null : carrinho.getUsuario().getId(),
                itens,
                carrinho.getTotal(),
                link);
    }

    public CarrinhoLista carrinhoToCarrinhoLista(Carrinho carrinho) {
        Link link = linkTo(methodOn(CarrinhoController.class).readCarrinho(carrinho.getId()))
                .withRel("Detalhes do carrinho");
        return new CarrinhoLista(carrinho.getId(), carrinho.getItens().size(), carrinho.getTotal(), link);
    }

    public ItemCarrinhoResponse itemToResponse(ItemCarrinho item) {
        return new ItemCarrinhoResponse(item.getId(),
                item.getProduto() == null ? null : item.getProduto().getId(),
                item.getProduto() == null ? null : item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal());
    }
}
