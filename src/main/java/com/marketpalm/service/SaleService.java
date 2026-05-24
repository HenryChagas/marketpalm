package com.marketpalm.service;

import com.marketpalm.dto.ProdutoMaisVendidoDTO;
import com.marketpalm.dto.ResumoFinanceiroDTO;
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

    public ResumoFinanceiroDTO obterResumoFinanceiro(LocalDateTime inicio, LocalDateTime fim) {
        BigDecimal faturamento = saleRepository.calcularFaturamentoPeriodo(inicio, fim);
        Long totalVendas = saleRepository.contarVendasPeriodo(inicio, fim);

        // Trata o caso de não haver vendas no período para não dar erro de NullPointerException
        if (faturamento == null) faturamento = BigDecimal.ZERO;
        if (totalVendas == null) totalVendas = 0L;

        // Cálculo do Ticket Médio: Faturamento / Total de Vendas
        BigDecimal ticketMedio = BigDecimal.ZERO;
        if (totalVendas > 0) {
            ticketMedio = faturamento.divide(BigDecimal.valueOf(totalVendas), 2, java.math.RoundingMode.HALF_UP);
        }

        return new ResumoFinanceiroDTO(faturamento, totalVendas, ticketMedio);
    }

    public List<ProdutoMaisVendidoDTO> obterProdutosMaisVendidos(LocalDateTime inicio, LocalDateTime fim) {
        return saleRepository.buscarProdutosMaisVendidos(inicio, fim);
    }
}