package com.marketpalm.dto;

import java.math.BigDecimal;

public record CategoriaMaisVendidaDTO(
        String nomeCategoria,
        Long quantidadeVendida,
        BigDecimal valorTotalFaturado
) {}