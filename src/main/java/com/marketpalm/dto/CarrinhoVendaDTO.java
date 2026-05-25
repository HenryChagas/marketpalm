package com.marketpalm.dto;

import java.util.List;

public record CarrinhoVendaDTO(
        List<ItemCarrinhoDTO> itens
) {
}