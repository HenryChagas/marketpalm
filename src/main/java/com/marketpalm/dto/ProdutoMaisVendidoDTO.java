package com.marketpalm.dto;

import java.math.BigDecimal;

public record ProdutoMaisVendidoDTO(String nomeProduto, Long quantidadeVendida, BigDecimal valorTotalFaturado) {
}
