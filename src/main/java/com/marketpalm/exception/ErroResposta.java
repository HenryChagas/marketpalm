package com.marketpalm.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErroResposta {
    private String mensagem;
    private int status;
}