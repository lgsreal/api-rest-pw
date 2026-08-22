package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.dto.ProdutoLista;
import br.com.fiap.api_rest.dto.ProdutoRequest;
import br.com.fiap.api_rest.dto.ProdutoResponse;
import br.com.fiap.api_rest.exception.RecursoNaoEncontradoException;
import br.com.fiap.api_rest.mapper.ProdutoMapper;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
    }

    // CRUD
    public Produto create(ProdutoRequest produtoRequest) {
        Produto produto = new Produto();
        produto.setNome(produtoRequest.nome());
        produto.setPreco(produtoRequest.preco());
        produto.setExpiracao(produtoRequest.expiracao());
        return produtoRepository.save(produto);
    }

    public ProdutoResponse read(UUID id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto n\u00e3o encontrado: " + id));
        return produtoMapper.produtoToResponse(produto);
    }

    // Page, Pageable
    public Page<ProdutoLista> read(Pageable pageable) {
        return produtoRepository
                .findAll(pageable)
                .map(produtoMapper::produtoToProdutoLista);
    }

    /*
    // Exemplo usando for em vez de stream
    public List<ProdutoResponse> read() {
        List<Produto> produtos = produtoRepository.findAll();
        List<ProdutoResponse> produtosResponse = new ArrayList<>();
        for (Produto produto : produtos) {
            produtosResponse.add(produtoMapper.produtoToResponse(produto));
        }
        return produtosResponse;
    }
     */

    public Produto update(Produto produto) {
        if (produto.getId() == null || !produtoRepository.existsById(produto.getId())) {
            throw new RecursoNaoEncontradoException("Produto n\u00e3o encontrado: " + produto.getId());
        }
        return produtoRepository.save(produto);
    }

    public void delete(UUID id) {
        if (!produtoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto n\u00e3o encontrado: " + id);
        }
        produtoRepository.deleteById(id);
    }
}
