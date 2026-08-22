package br.com.fiap.api_rest.dto;

import java.time.LocalDateTime;

public record ErroResponse(LocalDateTime timestamp, int status, String mensagem, String path) {
}
