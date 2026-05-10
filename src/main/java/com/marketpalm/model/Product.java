package com.marketpalm.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity // Diz ao Spring que esta classe deve virar uma tabela no banco
@Table(name = "products") // Define o nome da tabela no PostgreSQL
@Data // O Lombok gera automaticamente Getters, Setters e toString
public class Product {

    @Id // Define que este campo é a Chave Primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco gera o ID automaticamente (1, 2, 3...)
    private Long id;

    @Column(unique = true, nullable = false) // O código de barras não pode repetir e é obrigatório
    private String barcode;

    private String name;

    private BigDecimal price; // Usamos BigDecimal para dinheiro (mais precisão que Double)

    private Integer stock;
}