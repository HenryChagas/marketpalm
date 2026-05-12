package com.marketpalm.controller;

import com.marketpalm.model.Sale;
import com.marketpalm.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

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
}