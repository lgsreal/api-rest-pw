package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.ProdutoRequest;
import br.com.fiap.api_rest.dto.ProdutoResponse;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> readProduto() {
        List<ProdutoResponse> produtos = produtoService.read();
        if (produtos.isEmpty()) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(produtoService.read(), HttpStatus.OK);
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