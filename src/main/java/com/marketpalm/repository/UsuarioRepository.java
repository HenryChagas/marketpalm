package com.marketpalm.repository;

import com.marketpalm.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método essencial para o login: buscar o usuário pelo nome
    Optional<Usuario> findByLogin(String login);
}