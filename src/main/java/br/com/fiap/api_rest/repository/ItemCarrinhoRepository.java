package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, UUID> {
    List<ItemCarrinho> findByCarrinhoId(UUID carrinhoId);
}
