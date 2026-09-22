package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, UUID> {
    List<MovimentacaoEstoque> findByEstoqueIdOrderByDataHoraDesc(UUID estoqueId);
}
