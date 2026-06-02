package com.marketpalm.controller;

import com.marketpalm.dto.*;
import com.marketpalm.model.Sale;
import com.marketpalm.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    // --- Endpoints existentes ---

    // POST /api/sales
    @PostMapping
    public ResponseEntity<Sale> criarVenda(@RequestBody CarrinhoVendaDTO dto) {
        Sale novaVenda = saleService.realizarVenda(dto);
        return ResponseEntity.ok(novaVenda);
    }

    // GET /api/sales
    @GetMapping
    public List<Sale> listarTodas() {
        return saleService.listarVendas();
    }

    // GET /api/sales/data?dia=2026-05-10
    @GetMapping("/data")
    public List<Sale> listarPorData(@RequestParam("dia") String diaStr) {
        java.time.LocalDate data = java.time.LocalDate.parse(diaStr);
        return saleService.listarVendasDoDia(data);
    }

    // GET /api/sales/faturamento?dia=2026-05-10
    @GetMapping("/faturamento")
    public BigDecimal getFaturamentoPorDia(@RequestParam("dia") String diaStr) {
        java.time.LocalDate data = java.time.LocalDate.parse(diaStr);
        return saleService.calcularTotalVendidoNoDia(data);
    }

    // GET /api/sales/resumo?dias=30
    @GetMapping("/resumo")
    public ResponseEntity<ResumoFinanceiroDTO> buscarResumo(@RequestParam(defaultValue = "30") int dias) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(dias);
        return ResponseEntity.ok(saleService.obterResumoFinanceiro(inicio, fim));
    }

    // GET /api/sales/produtos-mais-vendidos?dias=30
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<ProdutoMaisVendidoDTO>> buscarProdutosMaisVendidos(@RequestParam(defaultValue = "30") int dias) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(dias);
        return ResponseEntity.ok(saleService.obterProdutosMaisVendidos(inicio, fim));
    }

    // --- Novos endpoints para o dashboard ---

    // GET /api/sales/hoje
    // KPI: total faturado no dia atual
    @GetMapping("/hoje")
    public ResponseEntity<BigDecimal> getFaturamentoHoje() {
        return ResponseEntity.ok(saleService.obterFaturamentoHoje());
    }

    // GET /api/sales/por-dia-semana?dias=7
    // Gráfico: faturamento agrupado por dia da semana
    @GetMapping("/por-dia-semana")
    public ResponseEntity<List<VendasPorDiaDTO>> getVendasPorDiaSemana(@RequestParam(defaultValue = "7") int dias) {
        return ResponseEntity.ok(saleService.obterVendasPorDiaSemana(dias));
    }

    // GET /api/sales/por-mes?ano=2026
    // Gráfico: faturamento mensal de um ano (para comparação esse ano vs anterior)
    @GetMapping("/por-mes")
    public ResponseEntity<List<VendasPorMesDTO>> getVendasPorMes(@RequestParam(defaultValue = "0") int ano) {
        int anoConsulta = (ano == 0) ? java.time.LocalDate.now().getYear() : ano;
        return ResponseEntity.ok(saleService.obterVendasPorMes(anoConsulta));
    }

    // GET /api/sales/categorias-mais-vendidas?dias=30
    // Painel: categorias mais vendidas por faturamento
    @GetMapping("/categorias-mais-vendidas")
    public ResponseEntity<List<CategoriaMaisVendidaDTO>> getCategoriasMaisVendidas(@RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(saleService.obterCategoriasMaisVendidas(dias));
    }
}