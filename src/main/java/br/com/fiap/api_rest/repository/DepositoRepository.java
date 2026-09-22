package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepositoRepository extends JpaRepository<Deposito, UUID> {
}
