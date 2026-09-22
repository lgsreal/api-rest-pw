package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.ProdutoLista;
import br.com.fiap.api_rest.dto.ProdutoRequest;
import br.com.fiap.api_rest.dto.ProdutoResponse;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

/**
 * Controller REST responsável pelas operações de CRUD de {@link Produto}.
 *
 * <p>Todos os endpoints são expostos sob o caminho base {@code /produtos} e
 * delegam a lógica de negócio para {@link ProdutoService}. A documentação
 * OpenAPI gerada fica disponível em {@code /swagger-ui/index.html}.</p>
 *
 * <p>Endpoints disponíveis:</p>
 * <ul>
 *   <li>{@code POST   /produtos}      - cria um produto</li>
 *   <li>{@code GET    /produtos/{id}} - busca um produto por id</li>
 *   <li>{@code GET    /produtos}      - lista produtos paginados</li>
 *   <li>{@code PUT    /produtos}      - atualiza um produto existente</li>
 *   <li>{@code DELETE /produtos/{id}} - exclui um produto por id</li>
 * </ul>
 */
@RestController
@RequestMapping("/produtos")
@Tag(name = "api-produtos", description = "Operações de cadastro, consulta, atualização e exclusão de produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    /**
     * Cria o controller com o serviço de produtos injetado pelo Spring.
     *
     * @param produtoService serviço responsável pela lógica de negócio de produtos
     */
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Cria um novo produto.
     *
     * @param produto dados do produto a ser criado, validados via Bean Validation
     * @return {@code 201 Created} com o produto persistido (incluindo o id gerado);
     *         {@code 400 Bad Request} caso a validação do corpo falhe
     */
    @Operation(summary = "Cria um novo produto",
               description = "Persiste um novo produto a partir dos dados informados no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no corpo da requisição",
                         content = @Content(schema = @Schema()))
    })
    @PostMapping
    public ResponseEntity<Produto> createProduto(@Valid @RequestBody ProdutoRequest produto) {
        Produto produtoSalvo = produtoService.create(produto);
        return new ResponseEntity<>(produtoSalvo, HttpStatus.CREATED);
    }

    /**
     * Busca um produto pelo seu identificador.
     *
     * @param id identificador (UUID) do produto
     * @return {@code 200 OK} com o produto encontrado ou
     *         {@code 204 No Content} caso não exista produto com o id informado
     */
    @Operation(summary = "Busca um produto por id",
               description = "Retorna os dados de um produto a partir do seu identificador UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ProdutoResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum produto encontrado para o id informado",
                         content = @Content(schema = @Schema()))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> readProduto(
            @Parameter(description = "Identificador UUID do produto", required = true)
            @PathVariable UUID id) {
        ProdutoResponse produto = produtoService.read(id);
        if (produto == null) {
            return new ResponseEntity<>(produto, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(produto, HttpStatus.OK);
    }

    /**
     * Lista produtos de forma paginada.
     *
     * <p>Cada página contém 2 itens ordenados pelo nome em ordem crescente.
     * Exemplo: {@code GET /produtos?pageNumber=0}.</p>
     *
     * @param pageNumber número da página (base 0); padrão {@code 0}
     * @return {@code 200 OK} com a página de produtos ou
     *         {@code 404 Not Found} caso a página esteja vazia
     */
    @Operation(summary = "Busca produtos por página",
               description = "Retorna uma página de produtos (2 itens por página) ordenada pelo nome.")
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
    public ResponseEntity<Page<ProdutoLista>> readProduto(
            @Parameter(description = "Número da página (iniciando em 0)", example = "0")
            @RequestParam(defaultValue = "0") Integer pageNumber) {
        // page number, page size, sort
        Pageable pageable = PageRequest.of(pageNumber, 2, Sort.by("nome").ascending());
        Page<ProdutoLista> produtos = produtoService.read(pageable);
        if (produtos.isEmpty()) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    /**
     * Atualiza um produto existente.
     *
     * <p>O produto é identificado pelo campo {@code id} presente no corpo da requisição.</p>
     *
     * @param produto produto com os dados atualizados (deve conter o id)
     * @return {@code 201 Created} com o produto atualizado ou
     *         {@code 400 Bad Request} caso não exista produto com o id informado
     */
    @Operation(summary = "Atualiza um produto específico",
               description = "Atualiza os dados de um produto existente, identificado pelo id informado no corpo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto atualizado com sucesso",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "400", description = "Produto não encontrado para o id informado",
                         content = @Content(schema = @Schema()))
    })
    @PutMapping
    public ResponseEntity<Produto> updateProduto(@RequestBody Produto produto) {
        ProdutoResponse produtoExistente = produtoService.read(produto.getId());
        if (produtoExistente == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Produto produtoAtualizado = produtoService.update(produto);
        return new ResponseEntity<>(produtoAtualizado, HttpStatus.CREATED);
    }

    /**
     * Exclui um produto pelo seu identificador.
     *
     * @param id identificador (UUID) do produto a ser excluído
     * @return {@code 200 OK} sem corpo após a exclusão
     */
    @Operation(summary = "Exclui um produto por id",
               description = "Remove o produto correspondente ao identificador UUID informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto excluído com sucesso",
                         content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(
            @Parameter(description = "Identificador UUID do produto", required = true)
            @PathVariable UUID id) {
        produtoService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}