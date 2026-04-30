package br.com.fiap.api_rest.dto;

import br.com.fiap.api_rest.model.UserRole;

public record RegisterDTO(String login, String senha, UserRole role) {
}
