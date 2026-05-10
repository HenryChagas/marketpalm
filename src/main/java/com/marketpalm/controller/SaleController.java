package com.marketpalm.controller;

import com.marketpalm.model.Sale;
import com.marketpalm.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping
    public List<Sale> listarTodas() {
        return saleService.listarVendas();
    }
}