package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.ProdutoLista;
import br.com.fiap.api_rest.dto.ProdutoRequest;
import br.com.fiap.api_rest.dto.ProdutoResponse;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<Produto> createProduto(@Valid @RequestBody ProdutoRequest produto) {
        Produto produtoSalvo = produtoService.create(produto);
        return new ResponseEntity<>(produtoSalvo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> readProduto(@PathVariable UUID id) {
        ProdutoResponse produto = produtoService.read(id);
        if (produto == null) {
            return new ResponseEntity<>(produto, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(produto, HttpStatus.OK);
    }

    // @PathVariable localhost:8080/produtos/1
    // @RequestParam localhost:8080/produtos?pageNumber=0

    // HATEOAS
    // PageAnterior: localhost:8080/produtos?pageNumber=0
    // PageSeguinte: null
    @GetMapping
    public ResponseEntity<Page<ProdutoLista>> readProduto(@RequestParam(defaultValue = "0") Integer pageNumber) {
        // page number, page size, sort
        Pageable pageable = PageRequest.of(pageNumber, 2, Sort.by("nome").ascending());
        Page<ProdutoLista> produtos = produtoService.read(pageable);
        if (produtos.isEmpty()) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Produto> updateProduto(@RequestBody Produto produto) {
        ProdutoResponse produtoExistente = produtoService.read(produto.getId());
        if (produtoExistente == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Produto produtoAtualizado = produtoService.update(produto);
        return new ResponseEntity<>(produtoAtualizado, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable UUID id) {
        produtoService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}