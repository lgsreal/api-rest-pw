package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.dto.ProdutoLista;
import br.com.fiap.api_rest.dto.ProdutoRequest;
import br.com.fiap.api_rest.dto.ProdutoResponse;
import br.com.fiap.api_rest.mapper.ProdutoMapper;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import org.springframework.beans.BeanUtils;
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
        BeanUtils.copyProperties(produtoRequest, produto);
        return produtoRepository.save(produto);
    }

    public ProdutoResponse read(UUID id) {
        return produtoRepository.findById(id)
                .map(produtoMapper::produtoToResponse)
                .orElse(null);
    }

    // Page, Pageable
    public Page<ProdutoLista> read(Pageable pageable) {
        return produtoRepository
                .findAll(pageable)
                .map(produtoMapper::produtoToProdutoLista);
    }

    public Produto update(Produto produto) {
        return produtoRepository.save(produto);
    }

    public void delete(UUID id) {
        produtoRepository.deleteById(id);
    }
}
