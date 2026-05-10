package com.marketpalm.service;

import com.marketpalm.model.Sale;
import com.marketpalm.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    public List<Sale> listarVendas() {
        return saleRepository.findAll();
    }
    public List<Sale> listarVendasDoDia(java.time.LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay(); // 2026-05-10 00:00:00
        LocalDateTime fim = data.atTime(23, 59, 59); // 2026-05-10 23:59:59
        return saleRepository.findBySaleDateBetween(inicio, fim);
    }

    public BigDecimal calcularTotalVendidoNoDia(java.time.LocalDate data) {
        List<Sale> vendas = listarVendasDoDia(data);
        return vendas.stream()
                .map(Sale::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}