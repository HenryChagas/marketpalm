package com.marketpalm.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login; // Ex: "admin" ou "totem1"

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String role; // "ROLE_ADMIN" (Dono) ou "ROLE_USER" (Máquininha)
}