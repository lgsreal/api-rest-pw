package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.model.UserRole;
import br.com.fiap.api_rest.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "segredo-de-teste");
    }

    @Test
    void deveGerarEValidarToken() {
        Usuario usuario = new Usuario("admin", "senha", UserRole.ADMIN);

        String token = tokenService.generateToken(usuario);

        assertThat(token).isNotBlank();
        assertThat(tokenService.validateToken(token)).isEqualTo(usuario.getUsername());
    }

    @Test
    void deveFalharAoValidarTokenInvalido() {
        assertThatThrownBy(() -> tokenService.validateToken("token-invalido"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void deveFalharAoValidarTokenAssinadoComOutroSegredo() {
        TokenService outroService = new TokenService();
        ReflectionTestUtils.setField(outroService, "secret", "outro-segredo");
        String token = outroService.generateToken(new Usuario("admin", "senha", UserRole.ADMIN));

        assertThatThrownBy(() -> tokenService.validateToken(token))
                .isInstanceOf(RuntimeException.class);
    }
}
