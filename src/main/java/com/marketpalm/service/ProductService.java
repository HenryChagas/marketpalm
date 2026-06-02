package com.marketpalm.service;

import com.marketpalm.model.Category;
import com.marketpalm.model.Product;
import com.marketpalm.repository.CategoryRepository;
import com.marketpalm.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ProductService {

    @Autowired // O Spring injeta o repositório aqui automaticamente
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Product salvarProduto(Product product) {
        // Regra Profissional: Validar se o preço é positivo
        if (product.getPrice().doubleValue() <= 0) {
            throw new RuntimeException("O preço deve ser maior que zero!");
        }

        // NOVA LÓGICA: Se o produto veio com uma categoria e essa categoria tem um ID...
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            // Buscamos a categoria completa no banco (que tem o nome preenchido)
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada!"));

            // Associamos a categoria completa (com ID e Nome) ao produto antes de salvar
            product.setCategory(category);
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

    public void deletarProduto(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Produto não encontrado!");
        }
        productRepository.deleteById(id);
    }

    public List<Product> listarPorCategoria(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

}