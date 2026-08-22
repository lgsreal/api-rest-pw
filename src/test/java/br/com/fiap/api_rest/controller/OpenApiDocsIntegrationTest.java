package br.com.fiap.api_rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OpenApiDocsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveDocumentarEndpointsDeEstoqueECarrinho() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/estoques'].post.summary").exists())
                .andExpect(jsonPath("$.paths['/estoques'].post.responses['403']").exists())
                .andExpect(jsonPath("$.paths['/estoques'].get.summary").exists())
                .andExpect(jsonPath("$.paths['/estoques/{id}'].put.summary").exists())
                .andExpect(jsonPath("$.paths['/estoques/{id}'].delete.summary").exists())
                .andExpect(jsonPath("$.paths['/estoques/produto/{produtoId}'].get.summary").exists())
                .andExpect(jsonPath("$.paths['/carrinhos'].post.summary").exists())
                .andExpect(jsonPath("$.paths['/carrinhos'].post.responses['409']").exists())
                .andExpect(jsonPath("$.paths['/carrinhos'].get.summary").exists())
                .andExpect(jsonPath("$.paths['/carrinhos/{id}'].get.summary").exists())
                .andExpect(jsonPath("$.paths['/carrinhos/{id}'].put.summary").exists())
                .andExpect(jsonPath("$.paths['/carrinhos/{id}'].delete.summary").exists())
                .andExpect(jsonPath("$.paths['/estoques'].post.tags[0]").value("api-estoques"))
                .andExpect(jsonPath("$.paths['/carrinhos'].post.tags[0]").value("api-carrinhos"));
    }
}
