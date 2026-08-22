package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.CarrinhoLista;
import br.com.fiap.api_rest.dto.CarrinhoRequest;
import br.com.fiap.api_rest.dto.CarrinhoResponse;
import br.com.fiap.api_rest.dto.ErroResponse;
import br.com.fiap.api_rest.service.CarrinhoService;
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
@RequestMapping("/carrinhos")
@Tag(name = "api-carrinhos", description = "Carrinhos de compras (somente ADMIN)")
public class CarrinhoController {
    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @Operation(summary = "Cria um carrinho de compras validando o estoque dos produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carrinho criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usu\u00e1rio, produto ou estoque n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CarrinhoResponse> createCarrinho(@Valid @RequestBody CarrinhoRequest carrinhoRequest) {
        return new ResponseEntity<>(carrinhoService.create(carrinhoRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Busca um carrinho por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrinho encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Carrinho n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CarrinhoResponse> readCarrinho(@PathVariable UUID id) {
        return new ResponseEntity<>(carrinhoService.read(id), HttpStatus.OK);
    }

    @Operation(summary = "Busca carrinhos por p\u00e1gina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "P\u00e1gina de carrinhos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarrinhoLista.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum carrinho encontrado",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping
    public ResponseEntity<Page<CarrinhoLista>> readCarrinhos(@RequestParam(defaultValue = "0") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        Page<CarrinhoLista> carrinhos = carrinhoService.read(pageable);
        if (carrinhos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(carrinhos, HttpStatus.OK);
    }

    @Operation(summary = "Atualiza os itens de um carrinho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrinho atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarrinhoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inv\u00e1lidos",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Carrinho, usu\u00e1rio ou produto n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CarrinhoResponse> updateCarrinho(@PathVariable UUID id,
                                                          @Valid @RequestBody CarrinhoRequest carrinhoRequest) {
        return new ResponseEntity<>(carrinhoService.update(id, carrinhoRequest), HttpStatus.OK);
    }

    @Operation(summary = "Exclui um carrinho por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrinho exclu\u00eddo com sucesso",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Carrinho n\u00e3o encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarrinho(@PathVariable UUID id) {
        carrinhoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
