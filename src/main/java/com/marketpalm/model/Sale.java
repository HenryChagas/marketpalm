package com.marketpalm.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Muitas vendas podem ter o mesmo produto
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime saleDate;
}