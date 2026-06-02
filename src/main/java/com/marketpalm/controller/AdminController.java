package com.marketpalm.controller;

import com.marketpalm.dto.*;
import com.marketpalm.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SaleService saleService;

    @GetMapping("/login")
    public String exibirLogin() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String exibirDashboard(@RequestParam(defaultValue = "30") int dias, Model model) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(dias);

        // KPIs
        ResumoFinanceiroDTO resumo = saleService.obterResumoFinanceiro(inicio, fim);
        BigDecimal vendasHoje = saleService.obterFaturamentoHoje();

        // Tabelas
        List<ProdutoMaisVendidoDTO> ranking = saleService.obterProdutosMaisVendidos(inicio, fim);
        List<CategoriaMaisVendidaDTO> categorias = saleService.obterCategoriasMaisVendidas(dias);

        // Gráfico: vendas por dia da semana — serializa labels e valores para JS
        List<VendasPorDiaDTO> vendasDia = saleService.obterVendasPorDiaSemana(dias);
        String labelsDia = vendasDia.stream()
                .map(v -> "\"" + v.diaSemana() + "\"")
                .reduce((a, b) -> a + "," + b).orElse("");
        String dadosDia = vendasDia.stream()
                .map(v -> v.totalVendas().toPlainString())
                .reduce((a, b) -> a + "," + b).orElse("");

        // Gráfico: vendas mensais — ano atual e ano anterior
        int anoAtual = java.time.LocalDate.now().getYear();
        List<VendasPorMesDTO> vendasMesAtual = saleService.obterVendasPorMes(anoAtual);
        List<VendasPorMesDTO> vendasMesAnterior = saleService.obterVendasPorMes(anoAtual - 1);

        // Monta arrays de 12 posições (índice 0=Jan ... 11=Dez), valor 0 se não houver dado
        BigDecimal[] arrAtual = new BigDecimal[12];
        BigDecimal[] arrAnterior = new BigDecimal[12];
        for (int i = 0; i < 12; i++) {
            arrAtual[i] = BigDecimal.ZERO;
            arrAnterior[i] = BigDecimal.ZERO;
        }
        for (VendasPorMesDTO v : vendasMesAtual)    arrAtual[v.mes().intValue() - 1]    = v.totalVendas();
        for (VendasPorMesDTO v : vendasMesAnterior) arrAnterior[v.mes().intValue() - 1] = v.totalVendas();

        StringBuilder sbAtual    = new StringBuilder();
        StringBuilder sbAnterior = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            if (i > 0) { sbAtual.append(","); sbAnterior.append(","); }
            sbAtual.append(arrAtual[i].toPlainString());
            sbAnterior.append(arrAnterior[i].toPlainString());
        }

        // Maior valor de categoria para calcular % das barras
        BigDecimal maxCategoria = categorias.isEmpty() ? BigDecimal.ONE
                : categorias.get(0).valorTotalFaturado();

        model.addAttribute("resumo", resumo);
        model.addAttribute("vendasHoje", vendasHoje);
        model.addAttribute("ranking", ranking);
        model.addAttribute("categorias", categorias);
        model.addAttribute("maxCategoria", maxCategoria);
        model.addAttribute("diasFiltro", dias);
        model.addAttribute("anoAtual", anoAtual);
        model.addAttribute("labelsDia", labelsDia);
        model.addAttribute("dadosDia", dadosDia);
        model.addAttribute("dadosMesAtual", sbAtual.toString());
        model.addAttribute("dadosMesAnterior", sbAnterior.toString());

        return "dashboard";
    }
}