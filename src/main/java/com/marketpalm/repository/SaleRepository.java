package com.marketpalm.repository;

import com.marketpalm.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Busca todas as vendas que aconteceram entre o início e o fim de um dia
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}