package br.com.fiap.api_rest.security;

import br.com.fiap.api_rest.repository.UsuarioRepository;
import br.com.fiap.api_rest.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);
    private static final String PREFIXO_BEARER = "Bearer ";

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarToken(request);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            autenticar(token);
        }
        filterChain.doFilter(request, response);
    }

    private void autenticar(String token) {
        String login;
        try {
            login = tokenService.validateToken(token);
        } catch (RuntimeException ex) {
            log.debug("Token invalido recebido", ex);
            return;
        }
        UserDetails usuario = usuarioRepository.findByLogin(login);
        if (usuario == null) {
            return;
        }
        var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(autenticacao);
    }

    private String recuperarToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(PREFIXO_BEARER)) {
            return null;
        }
        return header.substring(PREFIXO_BEARER.length()).trim();
    }
}
