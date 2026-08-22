package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.repository.EstoqueRepository;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sem @Transactional: a exclusao precisa ser confirmada apos o commit, ja que o
 * cascade de Produto para Estoque pode reverter o delete no flush.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EstoqueDeleteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private EstoqueRepository estoqueRepository;

    @AfterEach
    void limpar() {
        estoqueRepository.deleteAll();
        produtoRepository.deleteAll();
    }

    @Test
    void deveRemoverEstoqueDoBancoAoExcluir() throws Exception {
        Produto produto = new Produto();
        produto.setNome("Cafe Premium");
        produto.setPreco(new BigDecimal("25.50"));
        produto = produtoRepository.save(produto);

        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(10);
        estoque.setQuantidadeMinima(2);
        estoque = estoqueRepository.save(estoque);

        mockMvc.perform(delete("/estoques/{id}", estoque.getId()))
                .andExpect(status().isNoContent());

        assertThat(estoqueRepository.findById(estoque.getId())).isEmpty();
        assertThat(estoqueRepository.count()).isZero();
        assertThat(produtoRepository.findById(produto.getId())).isPresent();
    }
}
