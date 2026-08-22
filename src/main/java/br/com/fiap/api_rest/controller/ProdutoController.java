package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.ErroResponse;
import br.com.fiap.api_rest.dto.ProdutoLista;
import br.com.fiap.api_rest.dto.ProdutoRequest;
import br.com.fiap.api_rest.dto.ProdutoResponse;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/produtos")
@Tag(name = "api-produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Operation(summary = "Cria um novo produto")
    @PostMapping
    public ResponseEntity<Produto> createProduto(@Valid @RequestBody ProdutoRequest produto) {
        Produto produtoSalvo = produtoService.create(produto);
        return new ResponseEntity<>(produtoSalvo, HttpStatus.CREATED);
    }

    @Operation(summary = "Busca um produto por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Produto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Produto n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> readProduto(@PathVariable UUID id) {
        return new ResponseEntity<>(produtoService.read(id), HttpStatus.OK);
    }

    // @PathVariable localhost:8080/produtos/1
    // @RequestParam localhost:8080/produtos?pageNumber=0

    // HATEOAS
    // PageAnterior: localhost:8080/produtos?pageNumber=0
    // PageSeguinte: null
    @Operation(summary = "Busca produtos por página")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                            description = "Página de produtos retornada com sucesso!",
                            content = @Content(mediaType = "application/json",
                                                schema = @Schema(implementation = ProdutoLista.class)
                            )
            ),
            @ApiResponse(responseCode = "404",
                            description = "Nenhum produto encontrado",
                            content = @Content(schema = @Schema())
            )
    })
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

    @Operation(summary = "Atualiza um produto específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "404",
                    description = "Produto n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @PutMapping
    public ResponseEntity<Produto> updateProduto(@RequestBody Produto produto) {
        Produto produtoAtualizado = produtoService.update(produto);
        return new ResponseEntity<>(produtoAtualizado, HttpStatus.CREATED);
    }

    @Operation(summary = "Exclui um produto por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Produto exclu\u00eddo com sucesso",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404",
                    description = "Produto n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable UUID id) {
        produtoService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}