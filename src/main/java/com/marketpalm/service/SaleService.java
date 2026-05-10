package com.marketpalm.service;

import com.marketpalm.model.Sale;
import com.marketpalm.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    public List<Sale> listarVendas() {
        return saleRepository.findAll();
    }
}