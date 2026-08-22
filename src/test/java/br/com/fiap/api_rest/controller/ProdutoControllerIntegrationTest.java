package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.ProdutoRequest;
import br.com.fiap.api_rest.model.Produto;
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
import java.time.LocalDate;
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
class ProdutoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper objectMapper;
    @Autowired
    private ProdutoRepository produtoRepository;

    private Produto salvarProduto() {
        Produto produto = new Produto();
        produto.setNome("Cha verde");
        produto.setPreco(new BigDecimal("10.00"));
        return produtoRepository.save(produto);
    }

    @Test
    void deveCriarProduto() throws Exception {
        ProdutoRequest request = new ProdutoRequest("Cha verde", new BigDecimal("10.00"), LocalDate.of(2030, 1, 1));

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Cha verde"));
    }

    @Test
    void deveRejeitarProdutoInvalido() throws Exception {
        ProdutoRequest request = new ProdutoRequest("", new BigDecimal("0.10"), null);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").exists())
                .andExpect(jsonPath("$.preco").exists());
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        Produto produto = salvarProduto();

        mockMvc.perform(get("/produtos/{id}", produto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cha verde"));
    }

    @Test
    void deveRetornar404ParaProdutoInexistente() throws Exception {
        mockMvc.perform(get("/produtos/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void deveListarProdutosPaginados() throws Exception {
        salvarProduto();

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Cha verde"));
    }

    @Test
    void deveAtualizarProduto() throws Exception {
        Produto produto = salvarProduto();
        produto.setNome("Cha preto");

        mockMvc.perform(put("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Cha preto"));
    }

    @Test
    void deveRetornar404AoAtualizarProdutoInexistente() throws Exception {
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Cha preto");

        mockMvc.perform(put("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveExcluirProduto() throws Exception {
        Produto produto = salvarProduto();

        mockMvc.perform(delete("/produtos/{id}", produto.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/produtos/{id}", produto.getId()))
                .andExpect(status().isNotFound());
    }
}
