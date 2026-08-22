package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.dto.CarrinhoLista;
import br.com.fiap.api_rest.dto.CarrinhoRequest;
import br.com.fiap.api_rest.dto.CarrinhoResponse;
import br.com.fiap.api_rest.dto.ItemCarrinhoRequest;
import br.com.fiap.api_rest.exception.EstoqueInsuficienteException;
import br.com.fiap.api_rest.exception.RecursoNaoEncontradoException;
import br.com.fiap.api_rest.mapper.CarrinhoMapper;
import br.com.fiap.api_rest.model.Carrinho;
import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.ItemCarrinho;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.model.Usuario;
import br.com.fiap.api_rest.repository.CarrinhoRepository;
import br.com.fiap.api_rest.repository.EstoqueRepository;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import br.com.fiap.api_rest.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CarrinhoService {
    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarrinhoMapper carrinhoMapper;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           ProdutoRepository produtoRepository,
                           EstoqueRepository estoqueRepository,
                           UsuarioRepository usuarioRepository,
                           CarrinhoMapper carrinhoMapper) {
        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.usuarioRepository = usuarioRepository;
        this.carrinhoMapper = carrinhoMapper;
    }

    @Transactional
    public CarrinhoResponse create(CarrinhoRequest carrinhoRequest) {
        Carrinho carrinho = new Carrinho();
        carrinho.setUsuario(buscarUsuario(carrinhoRequest.usuarioId()));
        carrinhoRequest.itens().forEach(item -> carrinho.adicionarItem(criarItem(item)));
        return carrinhoMapper.carrinhoToResponse(carrinhoRepository.save(carrinho));
    }

    @Transactional(readOnly = true)
    public CarrinhoResponse read(UUID id) {
        return carrinhoMapper.carrinhoToResponse(buscarCarrinho(id));
    }

    @Transactional(readOnly = true)
    public Page<CarrinhoLista> read(Pageable pageable) {
        return carrinhoRepository.findAll(pageable).map(carrinhoMapper::carrinhoToCarrinhoLista);
    }

    @Transactional
    public CarrinhoResponse update(UUID id, CarrinhoRequest carrinhoRequest) {
        Carrinho carrinho = buscarCarrinho(id);
        carrinho.setUsuario(buscarUsuario(carrinhoRequest.usuarioId()));
        carrinho.getItens().clear();
        carrinhoRequest.itens().forEach(item -> carrinho.adicionarItem(criarItem(item)));
        return carrinhoMapper.carrinhoToResponse(carrinhoRepository.save(carrinho));
    }

    @Transactional
    public void delete(UUID id) {
        carrinhoRepository.delete(buscarCarrinho(id));
    }

    private ItemCarrinho criarItem(ItemCarrinhoRequest itemRequest) {
        Produto produto = produtoRepository.findById(itemRequest.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto n\u00e3o encontrado: " + itemRequest.produtoId()));
        validarEstoque(produto, itemRequest.quantidade());
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(produto);
        item.setQuantidade(itemRequest.quantidade());
        item.setPrecoUnitario(produto.getPreco());
        return item;
    }

    private void validarEstoque(Produto produto, int quantidade) {
        Estoque estoque = estoqueRepository.findByProdutoId(produto.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estoque n\u00e3o encontrado para o produto " + produto.getId()));
        if (estoque.getQuantidade() < quantidade) {
            throw new EstoqueInsuficienteException("Estoque insuficiente para o produto " + produto.getId()
                    + ": dispon\u00edvel " + estoque.getQuantidade() + ", solicitado " + quantidade);
        }
    }

    private Carrinho buscarCarrinho(UUID id) {
        return carrinhoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Carrinho n\u00e3o encontrado: " + id));
    }

    private Usuario buscarUsuario(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usu\u00e1rio n\u00e3o encontrado: " + id));
    }
}
