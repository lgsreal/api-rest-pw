package br.com.fiap.api_rest.dto;

import br.com.fiap.api_rest.model.TipoMovimentacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoResponse(UUID id, UUID estoqueId, TipoMovimentacao tipo, int quantidade,
                                   LocalDateTime dataHora) {
}
