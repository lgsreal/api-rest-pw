package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.EstoqueRequest;
import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.repository.EstoqueRepository;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class EstoqueControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper objectMapper;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private EstoqueRepository estoqueRepository;

    private Produto salvarProduto() {
        Produto produto = new Produto();
        produto.setNome("Cha verde");
        produto.setPreco(new BigDecimal("10.00"));
        return produtoRepository.save(produto);
    }

    private Estoque salvarEstoque(Produto produto, int quantidade) {
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(quantidade);
        return estoqueRepository.save(estoque);
    }

    @Test
    void deveCriarEstoque() throws Exception {
        Produto produto = salvarProduto();
        EstoqueRequest request = new EstoqueRequest(produto.getId(), 10, 2);

        mockMvc.perform(post("/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantidade").value(10))
                .andExpect(jsonPath("$.produtoId").value(produto.getId().toString()));
    }

    @Test
    void deveRejeitarEstoqueComQuantidadeNegativa() throws Exception {
        Produto produto = salvarProduto();
        EstoqueRequest request = new EstoqueRequest(produto.getId(), -1, 0);

        mockMvc.perform(post("/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantidade").exists());
    }

    @Test
    void deveRejeitarEstoqueSemProdutoId() throws Exception {
        EstoqueRequest request = new EstoqueRequest(null, 10, 0);

        mockMvc.perform(post("/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.produtoId").exists());
    }

    @Test
    void deveRetornar404AoCriarEstoqueDeProdutoInexistente() throws Exception {
        EstoqueRequest request = new EstoqueRequest(UUID.randomUUID(), 10, 0);

        mockMvc.perform(post("/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar422AoCriarEstoqueDuplicado() throws Exception {
        Produto produto = salvarProduto();
        salvarEstoque(produto, 5);
        EstoqueRequest request = new EstoqueRequest(produto.getId(), 10, 0);

        mockMvc.perform(post("/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void deveBuscarEstoquePorIdEPorProduto() throws Exception {
        Produto produto = salvarProduto();
        Estoque estoque = salvarEstoque(produto, 8);

        mockMvc.perform(get("/estoques/{id}", estoque.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(8));

        mockMvc.perform(get("/estoques/produto/{produtoId}", produto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtoNome").value("Cha verde"));
    }

    @Test
    void deveRetornar404ParaEstoqueInexistente() throws Exception {
        mockMvc.perform(get("/estoques/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void deveListarEstoquesPaginados() throws Exception {
        salvarEstoque(salvarProduto(), 3);

        mockMvc.perform(get("/estoques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].quantidade").value(3));
    }

    @Test
    void deveAtualizarEstoque() throws Exception {
        Produto produto = salvarProduto();
        Estoque estoque = salvarEstoque(produto, 3);
        EstoqueRequest request = new EstoqueRequest(produto.getId(), 30, 5);

        mockMvc.perform(put("/estoques/{id}", estoque.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(30))
                .andExpect(jsonPath("$.quantidadeMinima").value(5));
    }

    @Test
    void deveExcluirEstoque() throws Exception {
        Estoque estoque = salvarEstoque(salvarProduto(), 3);

        mockMvc.perform(delete("/estoques/{id}", estoque.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/estoques/{id}", estoque.getId()))
                .andExpect(status().isNotFound());
    }
}
