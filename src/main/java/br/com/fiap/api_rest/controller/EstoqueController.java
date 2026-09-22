package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.MovimentacaoRequest;
import br.com.fiap.api_rest.dto.MovimentacaoResponse;
import br.com.fiap.api_rest.dto.SaldoEstoqueResponse;
import br.com.fiap.api_rest.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estoques")
@Tag(name = "api-estoques")
public class EstoqueController {
    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @Operation(summary = "Registra uma movimentação de estoque (entrada, saída ou ajuste)")
    @PostMapping("/movimentacoes")
    public ResponseEntity<MovimentacaoResponse> registrarMovimentacao(@Valid @RequestBody MovimentacaoRequest request) {
        MovimentacaoResponse movimentacao = estoqueService.registrarMovimentacao(request);
        return new ResponseEntity<>(movimentacao, HttpStatus.CREATED);
    }

    @Operation(summary = "Consulta o saldo de um produto em um depósito")
    @GetMapping("/saldo")
    public ResponseEntity<SaldoEstoqueResponse> consultarSaldo(@RequestParam UUID produtoId,
                                                             @RequestParam UUID depositoId) {
        SaldoEstoqueResponse saldo = estoqueService.consultarSaldo(produtoId, depositoId);
        return new ResponseEntity<>(saldo, HttpStatus.OK);
    }

    @Operation(summary = "Lista o histórico de movimentações de um estoque")
    @GetMapping("/{estoqueId}/movimentacoes")
    public ResponseEntity<List<MovimentacaoResponse>> consultarHistorico(@PathVariable UUID estoqueId) {
        List<MovimentacaoResponse> historico = estoqueService.consultarHistorico(estoqueId);
        if (historico.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(historico, HttpStatus.OK);
    }
}
