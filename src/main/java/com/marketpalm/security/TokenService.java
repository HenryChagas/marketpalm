package com.marketpalm.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.marketpalm.model.Usuario;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Palavra-chave secreta para assinar o token (em produção, use variáveis de ambiente)
    private final String segredo = "marketpalm-secret-key-123456";

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(segredo);
            return JWT.create()
                    .withIssuer("marketpalm-api") // Quem emitiu o token
                    .withSubject(usuario.getLogin()) // O utilizador dono do token
                    .withClaim("role", usuario.getRole()) // O nível de acesso (ADMIN ou USER)
                    .withExpiresAt(gerarDataExpiracao()) // Tempo de validade da pulseira
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(segredo);
            return JWT.require(algorithm)
                    .withIssuer("marketpalm-api")
                    .build()
                    .verify(token)
                    .getSubject(); // Retorna o login do utilizador se estiver tudo OK
        } catch (JWTVerificationException exception) {
            return ""; // Se o token for inválido ou adulterado, retorna vazio
        }
    }

    private Instant gerarDataExpiracao() {
        // O token expira em 2 horas (fuso horário do Brasil/São Paulo)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}