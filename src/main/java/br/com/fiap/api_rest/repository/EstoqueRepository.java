package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {
    Optional<Estoque> findByProdutoId(UUID produtoId);
}
