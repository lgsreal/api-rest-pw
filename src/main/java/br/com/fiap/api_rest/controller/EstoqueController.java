package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.ErroResponse;
import br.com.fiap.api_rest.dto.EstoqueLista;
import br.com.fiap.api_rest.dto.EstoqueRequest;
import br.com.fiap.api_rest.dto.EstoqueResponse;
import br.com.fiap.api_rest.service.EstoqueService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/estoques")
@Tag(name = "api-estoques", description = "Gest\u00e3o de estoque dos produtos (somente ADMIN)")
public class EstoqueController {
    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @Operation(summary = "Cria o estoque de um produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estoque criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EstoqueResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "422", description = "Produto j\u00e1 possui estoque",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EstoqueResponse> createEstoque(@Valid @RequestBody EstoqueRequest estoqueRequest) {
        return new ResponseEntity<>(estoqueService.create(estoqueRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Busca um estoque por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EstoqueResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Estoque n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponse> readEstoque(@PathVariable UUID id) {
        return new ResponseEntity<>(estoqueService.read(id), HttpStatus.OK);
    }

    @Operation(summary = "Busca o estoque de um produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EstoqueResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Estoque n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<EstoqueResponse> readEstoquePorProduto(@PathVariable UUID produtoId) {
        return new ResponseEntity<>(estoqueService.readByProduto(produtoId), HttpStatus.OK);
    }

    @Operation(summary = "Busca estoques por p\u00e1gina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "P\u00e1gina de estoques retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EstoqueLista.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum estoque encontrado",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public ResponseEntity<Page<EstoqueLista>> readEstoques(@RequestParam(defaultValue = "0") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        Page<EstoqueLista> estoques = estoqueService.read(pageable);
        if (estoques.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(estoques, HttpStatus.OK);
    }

    @Operation(summary = "Atualiza um estoque existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EstoqueResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Estoque ou produto n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EstoqueResponse> updateEstoque(@PathVariable UUID id,
                                                        @Valid @RequestBody EstoqueRequest estoqueRequest) {
        return new ResponseEntity<>(estoqueService.update(id, estoqueRequest), HttpStatus.OK);
    }

    @Operation(summary = "Exclui um estoque por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estoque exclu\u00eddo com sucesso",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Estoque n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstoque(@PathVariable UUID id) {
        estoqueService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
