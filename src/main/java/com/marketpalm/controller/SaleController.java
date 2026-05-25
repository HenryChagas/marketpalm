package com.marketpalm.controller;

import com.marketpalm.dto.ProdutoMaisVendidoDTO;
import com.marketpalm.dto.ResumoFinanceiroDTO;
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

    // Criar Venda: POST http://localhost:8080/api/sales
    @PostMapping
    public ResponseEntity<Sale> criarVenda(@RequestBody com.marketpalm.dto.CarrinhoVendaDTO dto) {
        Sale novaVenda = saleService.realizarVenda(dto);
        return ResponseEntity.ok(novaVenda);
    }

    @GetMapping
    public List<Sale> listarTodas() {
        return saleService.listarVendas();
    }

    // Buscar por data: GET http://localhost:8080/api/sales/data?dia=2026-05-10
    @GetMapping("/data")
    public List<Sale> listarPorData(@RequestParam("dia") String diaStr) {
        java.time.LocalDate data = java.time.LocalDate.parse(diaStr);
        return saleService.listarVendasDoDia(data);
    }

    // Total faturado: GET http://localhost:8080/api/sales/faturamento?dia=2026-05-10
    @GetMapping("/faturamento")
    public BigDecimal getFaturamentoPorDia(@RequestParam("dia") String diaStr) {
        java.time.LocalDate data = java.time.LocalDate.parse(diaStr);
        return saleService.calcularTotalVendidoNoDia(data);
    }

    // GET http://localhost:8080/api/sales/resumo?dias=30
    @GetMapping("/resumo")
    public ResponseEntity<ResumoFinanceiroDTO> buscarResumo(@RequestParam(defaultValue = "30") int dias) {
        // Define o período com base nos dias passados (ex: últimos 30 dias igual ao filtro do TouchPay)
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(dias);

        ResumoFinanceiroDTO resumo = saleService.obterResumoFinanceiro(inicio, fim);
        return ResponseEntity.ok(resumo);
    }

    // GET http://localhost:8080/api/sales/produtos-mais-vendidos?dias=30
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<ProdutoMaisVendidoDTO>> buscarProdutosMaisVendidos(@RequestParam(defaultValue = "30") int dias) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(dias);

        List<ProdutoMaisVendidoDTO> ranking = saleService.obterProdutosMaisVendidos(inicio, fim);
        return ResponseEntity.ok(ranking);
    }
}