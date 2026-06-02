package com.marketpalm.dto;

import java.math.BigDecimal;

public record VendasPorMesDTO(
        Double mes,             // 1=Janeiro, 2=Fevereiro, ..., 12=Dezembro (EXTRACT MONTH)
        BigDecimal totalVendas
) {}