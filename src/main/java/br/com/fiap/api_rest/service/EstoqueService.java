package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.dto.EstoqueLista;
import br.com.fiap.api_rest.dto.EstoqueRequest;
import br.com.fiap.api_rest.dto.EstoqueResponse;
import br.com.fiap.api_rest.exception.RecursoNaoEncontradoException;
import br.com.fiap.api_rest.exception.RegraDeNegocioException;
import br.com.fiap.api_rest.mapper.EstoqueMapper;
import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.repository.EstoqueRepository;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EstoqueService {
    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueMapper estoqueMapper;

    public EstoqueService(EstoqueRepository estoqueRepository,
                          ProdutoRepository produtoRepository,
                          EstoqueMapper estoqueMapper) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueMapper = estoqueMapper;
    }

    public EstoqueResponse create(EstoqueRequest estoqueRequest) {
        Produto produto = buscarProduto(estoqueRequest.produtoId());
        estoqueRepository.findByProdutoId(produto.getId()).ifPresent(estoque -> {
            throw new RegraDeNegocioException("J\u00e1 existe estoque cadastrado para o produto " + produto.getId());
        });
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(estoqueRequest.quantidade());
        estoque.setQuantidadeMinima(estoqueRequest.quantidadeMinima() == null ? 0 : estoqueRequest.quantidadeMinima());
        return estoqueMapper.estoqueToResponse(estoqueRepository.save(estoque));
    }

    public EstoqueResponse read(UUID id) {
        return estoqueMapper.estoqueToResponse(buscarEstoque(id));
    }

    public EstoqueResponse readByProduto(UUID produtoId) {
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estoque n\u00e3o encontrado para o produto " + produtoId));
        return estoqueMapper.estoqueToResponse(estoque);
    }

    public Page<EstoqueLista> read(Pageable pageable) {
        return estoqueRepository.findAll(pageable).map(estoqueMapper::estoqueToEstoqueLista);
    }

    public EstoqueResponse update(UUID id, EstoqueRequest estoqueRequest) {
        Estoque estoque = buscarEstoque(id);
        Produto produto = buscarProduto(estoqueRequest.produtoId());
        estoque.setProduto(produto);
        estoque.setQuantidade(estoqueRequest.quantidade());
        estoque.setQuantidadeMinima(estoqueRequest.quantidadeMinima() == null ? 0 : estoqueRequest.quantidadeMinima());
        return estoqueMapper.estoqueToResponse(estoqueRepository.save(estoque));
    }

    public void delete(UUID id) {
        estoqueRepository.delete(buscarEstoque(id));
    }

    private Estoque buscarEstoque(UUID id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estoque n\u00e3o encontrado: " + id));
    }

    private Produto buscarProduto(UUID id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto n\u00e3o encontrado: " + id));
    }
}
