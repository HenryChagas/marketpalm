package com.marketpalm.security;

import com.marketpalm.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extrai o token do cabeçalho da requisição
        String token = recuperarToken(request);

        // 2. Se o token existir e for válido, autentica o usuário no Spring
        if (token != null) {
            String login = tokenService.validarToken(token);

            if (!login.isEmpty()) {
                usuarioRepository.findByLogin(login).ifPresent(usuario -> {
                    // Converte nosso modelo para o UserDetails do Spring Security
                    UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                            .username(usuario.getLogin())
                            .password(usuario.getSenha())
                            .roles(usuario.getRole().replace("ROLE_", ""))
                            .build();

                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // Diz ao Spring Security: "Este usuário está autenticado e tem essas permissões"
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }

        // 3. Continua o fluxo normal da requisição (vai para o Controller)
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}