package br.com.fiap.api_rest.security;

import br.com.fiap.api_rest.dto.AuthDTO;
import br.com.fiap.api_rest.dto.RegisterDTO;
import br.com.fiap.api_rest.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper objectMapper;

    private String token(String login, UserRole role) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterDTO(login, "senha123", role))))
                .andExpect(status().isOk());

        String corpo = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthDTO(login, "senha123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(corpo).get("token").asString();
    }

    @Test
    void deveLiberarEstoquesParaTokenDeAdmin() throws Exception {
        mockMvc.perform(get("/estoques").header("Authorization", "Bearer " + token("admin1", UserRole.ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveNegarEstoquesParaTokenDeUsuarioComum() throws Exception {
        mockMvc.perform(get("/estoques").header("Authorization", "Bearer " + token("user1", UserRole.USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveIgnorarTokenInvalidoEManterAcessoNegado() throws Exception {
        mockMvc.perform(get("/estoques").header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveResponderForbiddenParaSenhaIncorreta() throws Exception {
        token("admin2", UserRole.ADMIN);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthDTO("admin2", "senha-errada"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.path").value("/auth/login"));
    }

    @Test
    void deveResponderForbiddenParaLoginInexistente() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthDTO("nao-existe", "senha123"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveLiberarDocumentacaoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
