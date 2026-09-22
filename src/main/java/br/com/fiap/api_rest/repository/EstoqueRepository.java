package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.Estoque;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {
    Optional<Estoque> findByProdutoIdAndDepositoId(UUID produtoId, UUID depositoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Estoque e WHERE e.produto.id = :produtoId AND e.deposito.id = :depositoId")
    Optional<Estoque> findByProdutoIdAndDepositoIdForUpdate(@Param("produtoId") UUID produtoId,
                                                          @Param("depositoId") UUID depositoId);
}
