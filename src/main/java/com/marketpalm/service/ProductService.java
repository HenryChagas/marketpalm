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

    public Product buscarPorCodigo(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o código: " + barcode));
    }

    public Product baixarEstoque(String barcode, Integer quantidade) {
        Product product = buscarPorCodigo(barcode); // Reutilizamos a busca que já funciona!

        if (product.getStock() < quantidade) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + product.getName());
        }

        product.setStock(product.getStock() - quantidade);
        return productRepository.save(product);
    }
}