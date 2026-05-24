package com.marketpalm.repository;

import com.marketpalm.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Busca todas as vendas que aconteceram entre o início e o fim de um dia
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    // CORRIGIDO: Alterado de s.totalValue para s.valorTotal e s.dateTime para s.dataHora
    @Query("SELECT SUM(s.totalPrice) FROM Sale s WHERE s.saleDate BETWEEN :inicio AND :fim")
    BigDecimal calcularFaturamentoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // CORRIGIDO: Alterado de s.dateTime para s.dataHora
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :inicio AND :fim")
    Long contarVendasPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}