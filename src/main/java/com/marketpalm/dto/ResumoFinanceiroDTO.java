package com.marketpalm.dto;

import java.math.BigDecimal;

public record ResumoFinanceiroDTO(BigDecimal faturamentoTotal, Long totalVendas, BigDecimal ticketMedio) {
}
