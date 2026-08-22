package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CarrinhoRepository extends JpaRepository<Carrinho, UUID> {
    List<Carrinho> findByUsuarioId(UUID usuarioId);
}
