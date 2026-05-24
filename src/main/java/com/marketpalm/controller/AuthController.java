package com.marketpalm.controller;

import com.marketpalm.dto.AutenticacaoDTO;
import com.marketpalm.dto.RegistroDTO;
import com.marketpalm.model.Usuario;
import com.marketpalm.repository.UsuarioRepository;
import com.marketpalm.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AutenticacaoDTO data) {
        // 1. O Spring Security valida as credenciais (se a senha bater com o login)
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // 2. Buscamos o NOSSO Usuario do banco de dados para passar pro TokenService
        Usuario usuario = usuarioRepository.findByLogin(data.login())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação"));

        // 3. Agora sim, geramos o token JWT sem erros de cast
        var token = tokenService.gerarToken(usuario);

        // 4. Retorna a "pulseira VIP"
        return ResponseEntity.ok(token);
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody RegistroDTO data) {
        // Verificamos se o login já existe para não duplicar
        if (this.usuarioRepository.findByLogin(data.login()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuário já cadastrado!");
        }

        // Criptografamos a senha com BCrypt antes de salvar no banco!
        String senhaCriptografada = new BCryptPasswordEncoder().encode(data.senha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(data.login());
        novoUsuario.setSenha(senhaCriptografada);
        novoUsuario.setRole(data.role()); // Ex: "ROLE_ADMIN" ou "ROLE_USER"

        this.usuarioRepository.save(novoUsuario);

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }
}