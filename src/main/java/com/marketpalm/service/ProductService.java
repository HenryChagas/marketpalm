package com.marketpalm.service;

import com.marketpalm.model.Product;
import com.marketpalm.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired // O Spring injeta o repositório aqui automaticamente
    private ProductRepository productRepository;

    public Product salvarProduto(Product product) {
        // Regra Profissional: Validar se o preço é positivo
        if (product.getPrice().doubleValue() <= 0) {
            throw new RuntimeException("O preço deve ser maior que zero!");
        }
        return productRepository.save(product);
    }

    public List<Product> listarTodos() {
        return productRepository.findAll();
    }
}