package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.DepositoRequest;
import br.com.fiap.api_rest.model.Deposito;
import br.com.fiap.api_rest.service.DepositoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/depositos")
@Tag(name = "api-depositos")
public class DepositoController {
    private final DepositoService depositoService;

    public DepositoController(DepositoService depositoService) {
        this.depositoService = depositoService;
    }

    @Operation(summary = "Cria um novo depósito")
    @PostMapping
    public ResponseEntity<Deposito> createDeposito(@Valid @RequestBody DepositoRequest deposito) {
        Deposito depositoSalvo = depositoService.create(deposito);
        return new ResponseEntity<>(depositoSalvo, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todos os depósitos")
    @GetMapping
    public ResponseEntity<List<Deposito>> readDepositos() {
        List<Deposito> depositos = depositoService.readAll();
        if (depositos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(depositos, HttpStatus.OK);
    }
}
