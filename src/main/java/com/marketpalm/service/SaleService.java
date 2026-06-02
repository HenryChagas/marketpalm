package com.marketpalm.service;

import com.marketpalm.dto.*;
import com.marketpalm.model.ItemVenda;
import com.marketpalm.model.Product;
import com.marketpalm.model.Sale;
import com.marketpalm.repository.ProductRepository;
import com.marketpalm.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    // --- Métodos existentes ---

    @Transactional
    public Sale realizarVenda(CarrinhoVendaDTO carrinhoDTO) {
        Sale venda = new Sale();
        venda.setSaleDate(LocalDateTime.now());
        venda.setTotalPrice(BigDecimal.ZERO);

        List<ItemVenda> itensVenda = new ArrayList<>();
        BigDecimal precoTotalCarrinho = BigDecimal.ZERO;

        for (ItemCarrinhoDTO itemDTO : carrinhoDTO.itens()) {
            Product produto = productRepository.findById(itemDTO.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + itemDTO.produtoId()));

            produto.setStock(produto.getStock() - itemDTO.quantidade());
            productRepository.save(produto);

            ItemVenda item = new ItemVenda();
            item.setVenda(venda);
            item.setProduct(produto);
            item.setQuantity(itemDTO.quantidade());
            item.setPrecoUnitario(produto.getPrice());

            BigDecimal subtotal = produto.getPrice().multiply(BigDecimal.valueOf(itemDTO.quantidade()));
            precoTotalCarrinho = precoTotalCarrinho.add(subtotal);

            itensVenda.add(item);
        }

        venda.setItens(itensVenda);
        venda.setTotalPrice(precoTotalCarrinho);

        return saleRepository.save(venda);
    }

    public List<Sale> listarVendas() {
        return saleRepository.findAll();
    }

    public List<Sale> listarVendasDoDia(LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);
        return saleRepository.findBySaleDateBetween(inicio, fim);
    }

    public BigDecimal calcularTotalVendidoNoDia(LocalDate data) {
        List<Sale> vendas = listarVendasDoDia(data);
        return vendas.stream()
                .map(Sale::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public ResumoFinanceiroDTO obterResumoFinanceiro(LocalDateTime inicio, LocalDateTime fim) {
        BigDecimal faturamento = saleRepository.calcularFaturamentoPeriodo(inicio, fim);
        Long totalVendas = saleRepository.contarVendasPeriodo(inicio, fim);

        if (faturamento == null) faturamento = BigDecimal.ZERO;
        if (totalVendas == null) totalVendas = 0L;

        BigDecimal ticketMedio = BigDecimal.ZERO;
        if (totalVendas > 0) {
            ticketMedio = faturamento.divide(BigDecimal.valueOf(totalVendas), 2, java.math.RoundingMode.HALF_UP);
        }

        return new ResumoFinanceiroDTO(faturamento, totalVendas, ticketMedio);
    }

    public List<ProdutoMaisVendidoDTO> obterProdutosMaisVendidos(LocalDateTime inicio, LocalDateTime fim) {
        return saleRepository.buscarProdutosMaisVendidos(inicio, fim);
    }

    // --- Novos métodos para o dashboard ---

    /**
     * Converte Object[] da native query para VendasPorDiaDTO.
     * Colunas: [0]=dia_semana (String), [1]=numero_dia (Integer), [2]=total_vendas (BigDecimal)
     */
    public List<VendasPorDiaDTO> obterVendasPorDiaSemana(int dias) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(dias);

        return saleRepository.buscarVendasPorDiaSemanaRaw(inicio, fim)
                .stream()
                .map(row -> new VendasPorDiaDTO(
                        (String) row[0],
                        ((Number) row[1]).doubleValue(),
                        new BigDecimal(row[2].toString())
                ))
                .toList();
    }

    /**
     * Converte Object[] da native query para VendasPorMesDTO.
     * Colunas: [0]=mes (Integer), [1]=total_vendas (BigDecimal)
     */
    public List<VendasPorMesDTO> obterVendasPorMes(int ano) {
        return saleRepository.buscarVendasPorMesRaw(ano)
                .stream()
                .map(row -> new VendasPorMesDTO(
                        ((Number) row[0]).doubleValue(),
                        new BigDecimal(row[1].toString())
                ))
                .toList();
    }

    /**
     * Converte Object[] da native query para CategoriaMaisVendidaDTO.
     * Colunas: [0]=nome_categoria (String), [1]=quantidade_vendida (Long), [2]=valor_total_faturado (BigDecimal)
     */
    public List<CategoriaMaisVendidaDTO> obterCategoriasMaisVendidas(int dias) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(dias);

        return saleRepository.buscarCategoriasMaisVendidasRaw(inicio, fim)
                .stream()
                .map(row -> new CategoriaMaisVendidaDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        new BigDecimal(row[2].toString())
                ))
                .toList();
    }

    /**
     * Faturamento do dia atual — KPI "Vendas de hoje".
     */
    public BigDecimal obterFaturamentoHoje() {
        return calcularTotalVendidoNoDia(LocalDate.now());
    }
}