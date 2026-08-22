package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.AuthDTO;
import br.com.fiap.api_rest.dto.ErroResponse;
import br.com.fiap.api_rest.dto.LoginResponse;
import br.com.fiap.api_rest.dto.RegisterDTO;
import br.com.fiap.api_rest.model.Usuario;
import br.com.fiap.api_rest.repository.UsuarioRepository;
import br.com.fiap.api_rest.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "api-auth", description = "Registro e emissao de token JWT")
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository,
                          AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Autentica um usuario e devolve o token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token gerado"),
            @ApiResponse(responseCode = "403", description = "Credenciais invalidas",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthDTO authDTO) {
        var usuarioSenha = new UsernamePasswordAuthenticationToken(authDTO.login(), authDTO.senha());
        var auth = this.authenticationManager.authenticate(usuarioSenha);
        String token = tokenService.generateToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @Operation(summary = "Registra um novo usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado"),
            @ApiResponse(responseCode = "400", description = "Login ja utilizado")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO registerDTO) {
        if (usuarioRepository.findByLogin(registerDTO.login()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = passwordEncoder.encode(registerDTO.senha());
        usuarioRepository.save(new Usuario(registerDTO.login(), encryptedPassword, registerDTO.role()));
        return ResponseEntity.ok().build();
    }
}
