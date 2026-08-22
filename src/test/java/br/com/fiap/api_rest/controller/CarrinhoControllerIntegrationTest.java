package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.CarrinhoRequest;
import br.com.fiap.api_rest.dto.ItemCarrinhoRequest;
import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.model.UserRole;
import br.com.fiap.api_rest.model.Usuario;
import br.com.fiap.api_rest.repository.EstoqueRepository;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import br.com.fiap.api_rest.repository.UsuarioRepository;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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
class CarrinhoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper objectMapper;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario salvarUsuario() {
        return usuarioRepository.save(new Usuario("admin-" + UUID.randomUUID(), "senha", UserRole.ADMIN));
    }

    private Produto salvarProdutoComEstoque(int quantidade) {
        Produto produto = new Produto();
        produto.setNome("Cha verde");
        produto.setPreco(new BigDecimal("10.00"));
        produto = produtoRepository.save(produto);
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(quantidade);
        estoqueRepository.save(estoque);
        return produto;
    }

    private String json(Object valor) throws Exception {
        return objectMapper.writeValueAsString(valor);
    }

    @Test
    void deveCriarCarrinhoQuandoHouverEstoque() throws Exception {
        Usuario usuario = salvarUsuario();
        Produto produto = salvarProdutoComEstoque(10);
        CarrinhoRequest request = new CarrinhoRequest(usuario.getId(),
                List.of(new ItemCarrinhoRequest(produto.getId(), 3)));

        mockMvc.perform(post("/carrinhos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itens[0].quantidade").value(3))
                .andExpect(jsonPath("$.itens[0].precoUnitario").value(10.00))
                .andExpect(jsonPath("$.total").value(30.00));
    }

    @Test
    void deveRetornar409QuandoEstoqueInsuficiente() throws Exception {
        Usuario usuario = salvarUsuario();
        Produto produto = salvarProdutoComEstoque(2);
        CarrinhoRequest request = new CarrinhoRequest(usuario.getId(),
                List.of(new ItemCarrinhoRequest(produto.getId(), 5)));

        mockMvc.perform(post("/carrinhos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void deveRetornar404QuandoProdutoNaoExiste() throws Exception {
        Usuario usuario = salvarUsuario();
        CarrinhoRequest request = new CarrinhoRequest(usuario.getId(),
                List.of(new ItemCarrinhoRequest(UUID.randomUUID(), 1)));

        mockMvc.perform(post("/carrinhos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarCarrinhoSemItens() throws Exception {
        Usuario usuario = salvarUsuario();
        CarrinhoRequest request = new CarrinhoRequest(usuario.getId(), List.of());

        mockMvc.perform(post("/carrinhos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.itens").exists());
    }

    @Test
    void deveRejeitarItemComQuantidadeZero() throws Exception {
        Usuario usuario = salvarUsuario();
        Produto produto = salvarProdutoComEstoque(10);
        CarrinhoRequest request = new CarrinhoRequest(usuario.getId(),
                List.of(new ItemCarrinhoRequest(produto.getId(), 0)));

        mockMvc.perform(post("/carrinhos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveBuscarListarAtualizarEExcluirCarrinho() throws Exception {
        Usuario usuario = salvarUsuario();
        Produto produto = salvarProdutoComEstoque(10);
        CarrinhoRequest request = new CarrinhoRequest(usuario.getId(),
                List.of(new ItemCarrinhoRequest(produto.getId(), 2)));

        String body = mockMvc.perform(post("/carrinhos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String carrinhoId = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(get("/carrinhos/{id}", carrinhoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(20.00));

        mockMvc.perform(get("/carrinhos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].quantidadeItens").value(1));

        CarrinhoRequest atualizacao = new CarrinhoRequest(usuario.getId(),
                List.of(new ItemCarrinhoRequest(produto.getId(), 4)));
        mockMvc.perform(put("/carrinhos/{id}", carrinhoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(40.00));

        mockMvc.perform(delete("/carrinhos/{id}", carrinhoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/carrinhos/{id}", carrinhoId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404AoBuscarCarrinhoInexistente() throws Exception {
        mockMvc.perform(get("/carrinhos/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
