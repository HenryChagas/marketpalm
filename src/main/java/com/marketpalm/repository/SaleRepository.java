package com.marketpalm.repository;

import com.marketpalm.dto.ProdutoMaisVendidoDTO;
import com.marketpalm.dto.VendasPorDiaDTO;
import com.marketpalm.dto.VendasPorMesDTO;
import com.marketpalm.dto.CategoriaMaisVendidaDTO;
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

    // --- Consultas existentes (JPQL) ---

    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(s.totalPrice) FROM Sale s WHERE s.saleDate BETWEEN :inicio AND :fim")
    BigDecimal calcularFaturamentoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :inicio AND :fim")
    Long contarVendasPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT new com.marketpalm.dto.ProdutoMaisVendidoDTO(i.product.name, SUM(i.quantity), SUM(i.precoUnitario * i.quantity)) " +
            "FROM ItemVenda i " +
            "WHERE i.venda.saleDate BETWEEN :inicio AND :fim " +
            "GROUP BY i.product.name " +
            "ORDER BY SUM(i.quantity) DESC")
    List<ProdutoMaisVendidoDTO> buscarProdutosMaisVendidos(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // --- Novas consultas com SQL nativo (evita limitações do JPQL com EXTRACT) ---

    // Faturamento por dia da semana — retorna [dia_semana (text), num_dia (int), total (numeric)]
    // EXTRACT DOW: 0=Dom, 1=Seg, ..., 6=Sáb
    @Query(value = """
            SELECT
                to_char(s.sale_date, 'Dy')         AS dia_semana,
                EXTRACT(DOW FROM s.sale_date)      AS numero_dia,
                COALESCE(SUM(s.total_price), 0)    AS total_vendas
            FROM sales s
            WHERE s.sale_date BETWEEN :inicio AND :fim
            GROUP BY to_char(s.sale_date, 'Dy'), EXTRACT(DOW FROM s.sale_date)
            ORDER BY EXTRACT(DOW FROM s.sale_date)
            """, nativeQuery = true)
    List<Object[]> buscarVendasPorDiaSemanaRaw(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Faturamento mensal de um ano — retorna [mes (int), total (numeric)]
    @Query(value = """
            SELECT
                EXTRACT(MONTH FROM s.sale_date)    AS mes,
                COALESCE(SUM(s.total_price), 0)    AS total_vendas
            FROM sales s
            WHERE EXTRACT(YEAR FROM s.sale_date) = :ano
            GROUP BY EXTRACT(MONTH FROM s.sale_date)
            ORDER BY mes
            """, nativeQuery = true)
    List<Object[]> buscarVendasPorMesRaw(@Param("ano") int ano);

    // Categorias mais vendidas — retorna [categoria (text), quantidade (bigint), total (numeric)]
    @Query(value = """
            SELECT
                c.name                                          AS nome_categoria,
                COALESCE(SUM(iv.quantity), 0)                  AS quantidade_vendida,
                COALESCE(SUM(iv.preco_unitario * iv.quantity), 0) AS valor_total_faturado
            FROM itens_venda iv
            JOIN products p ON p.id = iv.produto_id
            JOIN categories c ON c.id = p.category_id
            JOIN sales s ON s.id = iv.venda_id
            WHERE s.sale_date BETWEEN :inicio AND :fim
            GROUP BY c.name
            ORDER BY valor_total_faturado DESC
            """, nativeQuery = true)
    List<Object[]> buscarCategoriasMaisVendidasRaw(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}