package com.marketpalm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GerenciadorDeExcecoes {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResposta> tratarErroDeNegocio(RuntimeException ex) {
        ErroResposta erro = new ErroResposta(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
    }
}