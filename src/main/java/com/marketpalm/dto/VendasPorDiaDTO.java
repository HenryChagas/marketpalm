package com.marketpalm.dto;

import java.math.BigDecimal;

public record VendasPorDiaDTO(
        String diaSemana,       // Ex: "Mon", "Tue" — vindo do to_char do Postgres
        Double numeroDia,       // 0=Dom, 1=Seg, ..., 6=Sáb (EXTRACT DOW)
        BigDecimal totalVendas
) {}