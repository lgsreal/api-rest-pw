package br.com.fiap.api_rest.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveNegarAcessoAEstoquesParaUsuarioSemRoleAdmin() throws Exception {
        mockMvc.perform(get("/estoques")).andExpect(status().isForbidden());
        mockMvc.perform(get("/estoques/{id}", UUID.randomUUID())).andExpect(status().isForbidden());
        mockMvc.perform(post("/estoques").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/estoques/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/estoques/{id}", UUID.randomUUID())).andExpect(status().isForbidden());
    }

    @Test
    void deveNegarAcessoACarrinhosParaUsuarioSemRoleAdmin() throws Exception {
        mockMvc.perform(get("/carrinhos")).andExpect(status().isForbidden());
        mockMvc.perform(get("/carrinhos/{id}", UUID.randomUUID())).andExpect(status().isForbidden());
        mockMvc.perform(post("/carrinhos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/carrinhos/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/carrinhos/{id}", UUID.randomUUID())).andExpect(status().isForbidden());
    }

    @Test
    void deveNegarCriacaoDeProdutoParaUsuarioSemRoleAdmin() throws Exception {
        mockMvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }
}
